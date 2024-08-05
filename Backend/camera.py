import subprocess

import cv2

input_size = 256

ip_camera_url = "http://192.168.0.102:4747/video?640x480"
cap = cv2.VideoCapture(ip_camera_url)

if not cap.isOpened():
    print(f"Error: Unable to open video stream from {ip_camera_url}")
    exit()

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
    '-preset', 'veryfast',
    '-f', 'mpegts',
    rtmp_url
]
process = subprocess.Popen(ffmpeg_cmd, stdin=subprocess.PIPE)


while cap.isOpened():
    ret, frame = cap.read()
    if not ret:
        break

    # Display the frame
    # Display the frame
    process.stdin.write(frame.toBytes())


cap.release()
process.stdin.close()
process.wait()
