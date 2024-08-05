import subprocess
import time

import cv2
import numpy as np
import tensorflow as tf
import tensorflow_hub as hub

# Check if GPU is available and set memory growth
gpus = tf.config.experimental.list_physical_devices('GPU')
if gpus:
    try:
        for gpu in gpus:
            tf.config.experimental.set_memory_growth(gpu, True)
        logical_gpus = tf.config.experimental.list_logical_devices('GPU')
        print(f"{len(gpus)} Physical GPUs, {len(logical_gpus)} Logical GPUs")
    except RuntimeError as e:
        print(e)
else:
    print("No GPU!")

model_name = "movenet_thunder"

if "movenet_lightning" in model_name:
    module = hub.load("https://tfhub.dev/google/movenet/singlepose/lightning/4")
    input_size = 192
elif "movenet_thunder" in model_name:
    module = hub.load("https://tfhub.dev/google/movenet/singlepose/thunder/4")
    input_size = 256
else:
    raise ValueError("Unsupported model name: %s" % model_name)


def movenet(input_image):
    """Runs detection on an input image.

    Args:
      input_image: A [1, height, width, 3] tensor represents the input image
        pixels. Note that the height/width should already be resized and match the
        expected input resolution of the model before passing into this function.

    Returns:
      A [1, 1, 17, 3] float numpy array representing the predicted keypoint
      coordinates and scores.
    """
    model = module.signatures['serving_default']

    # SavedModel format expects tensor type of int32.
    input_image = tf.cast(input_image, dtype=tf.int32)
    # Run model inference.
    outputs = model(input_image)
    # Output is a [1, 1, 17, 3] tensor.
    keypoints_with_scores = outputs['output_0'].numpy()
    return keypoints_with_scores


# Initialize the webcam
ip_camera_url = "http://192.168.0.102:4747/video?640x480"
cap = cv2.VideoCapture(ip_camera_url)

if not cap.isOpened():
    print(f"Error: Unable to open video stream from {ip_camera_url}")
    exit()

# Setup ffmpeg process
rtmp_url = "rtmp://177.3.74.79:1935/banana"
ffmpeg_cmd = [
    'ffmpeg',
    '-y',
    '-f', 'rawvideo',
    '-vcodec', 'rawvideo',
    '-pix_fmt', 'bgr24',
    '-s', f"{input_size}x{input_size}",
    '-r', '30',
    '-i', '-',
    '-c:v', 'libx264',
    '-pix_fmt', 'yuv420p',
    '-preset', 'ultrafast',
    '-tune', 'zerolatency',
    '-f', 'flv',
    rtmp_url
]
process = subprocess.Popen(ffmpeg_cmd, stdin=subprocess.PIPE)

while cap.isOpened():
    ret, frame = cap.read()
    if not ret:
        print("Error: Unable to read frame from video stream")
        break

    # Resize and preprocess the frame
    input_image = cv2.resize(frame, (input_size, input_size))
    input_image = np.expand_dims(input_image, axis=0)
    input_image = tf.convert_to_tensor(input_image, dtype=tf.float32)

    # Run the model
    keypoints_with_scores = movenet(input_image)

    # Draw keypoints on the frame
    for keypoint in keypoints_with_scores[0, 0, :, :]:
        y, x, confidence = keypoint
        if confidence > 0.5:
            cv2.circle(frame, (int(x * frame.shape[1]), int(y * frame.shape[0])), 5, (0, 255, 0), -1)

    frame_resized = cv2.resize(frame, (input_size, input_size))

    # Display the frame
    process.stdin.write(frame_resized.tobytes())

cap.release()
process.stdin.close()
process.wait()
