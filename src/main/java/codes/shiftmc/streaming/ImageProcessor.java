package codes.shiftmc.streaming;

import java.awt.image.BufferedImage;

public class ImageProcessor {

    private static final int[] mapColorPalette = {
            0xFF597D27, 0xFF6D9930, 0xFF7FB238, 0xFF435E1D,
            0xFFAEA473, 0xFFD5C98C, 0xFFF7E9A3, 0xFF827B56,
            0xFF8C8C8C, 0xFFABABAB, 0xFFC7C7C7, 0xFF696969,
            0xFFB40000, 0xFFDC0000, 0xFFFF0000, 0xFF870000,
            0xFF7070B4, 0xFF8A8ADC, 0xFFA0A0FF, 0xFF545487,
            0xFF757575, 0xFF909090, 0xFFA7A7A7, 0xFF585858,
            0xFF005700, 0xFF006A00, 0xFF007C00, 0xFF004100,
            0xFFB4B4B4, 0xFFDCDCDC, 0xFFFFFFFF, 0xFF878787,
            0xFF737681, 0xFF8D909E, 0xFFA4A8B8, 0xFF565861,
            0xFF6A4C36, 0xFF825E42, 0xFF976D4D, 0xFF4F3928,
            0xFF4F4F4F, 0xFF606060, 0xFF707070, 0xFF3B3B3B,
            0xFF2D2DB4, 0xFF3737DC, 0xFF4040FF, 0xFF212187,
            0xFF645432, 0xFF7B663E, 0xFF8F7748, 0xFF4B3F26,
            0xFFB4B1AC, 0xFFDCD9D3, 0xFFFFFFF5, 0xFF878581,
            0xFF985924, 0xFFBA6D2C, 0xFFD87F33, 0xFF72431B,
            0xFF7D3598, 0xFF9941BA, 0xFFB24CD8, 0xFF5E2872,
            0xFF486C98, 0xFF5884BA, 0xFF6699D8, 0xFF365172,
            0xFFA1A124, 0xFFC5C52C, 0xFFE5E533, 0xFF79791B,
            0xFF599011, 0xFF6DB015, 0xFF7FCC19, 0xFF436C0D,
            0xFFAA5974, 0xFFD06D8E, 0xFFF27FA5, 0xFF804357,
            0xFF353535, 0xFF414141, 0xFF4C4C4C, 0xFF282828,
            0xFF6C6C6C, 0xFF848484, 0xFF999999, 0xFF515151,
            0xFF35596C, 0xFF416D84, 0xFF4C7FB2, 0xFF284351,
            0xFF592C7D, 0xFF6D3699, 0xFF7F3FB2, 0xFF43215E,
            0xFF24357D, 0xFF2C4199, 0xFF334CB2, 0xFF1B285E,
            0xFF483524, 0xFF58412C, 0xFF664C33, 0xFF36281B,
            0xFF485924, 0xFF586D2C, 0xFF667F33, 0xFF36431B,
            0xFF6C2424, 0xFF842C2C, 0xFF993333, 0xFF511B1B,
            0xFF111111, 0xFF151515, 0xFF191919, 0xFF0D0D0D,
            0xFFB0A836, 0xFFD7CD42, 0xFFFAEE4D, 0xFF847E28,
            0xFF409A96, 0xFF4FBCCB, 0xFF5CD1D5, 0xFF307370,
            0xFF345AB4, 0xFF3F6EDC, 0xFF4A80FF, 0xFF274387,
            0xFF009928, 0xFF00BB32, 0xFF00D93A, 0xFF00721E,
            0xFF5B3C22, 0xFF6F4A2A, 0xFF815631, 0xFF442D19,
            0xFF4F0100, 0xFF600100, 0xFF700200, 0xFF3B0100,
            0xFF937C71, 0xFFB4988A, 0xFFD1B1A1, 0xFF6E5D55,
            0xFF703919, 0xFF89461F, 0xFF9F5224, 0xFF542B13,
            0xFF693D4C, 0xFF804B5D, 0xFF95576C, 0xFF4E2E39,
            0xFF4F4C61, 0xFF605D77, 0xFF706C8A, 0xFF3B3949,
            0xFF835D19, 0xFFA07220, 0xFFBA8527, 0xFF624613,
            0xFF485225, 0xFF58642D, 0xFF677535, 0xFF363D1C,
            0xFF703637, 0xFF8A4243, 0xFFA04D4E, 0xFF542829,
            0xFF281C18, 0xFF31231E, 0xFF392923, 0xFF1E1512,
            0xFF5F4B45, 0xFF745C54, 0xFF876B62, 0xFF473833,
            0xFF3D4040, 0xFF4B4F4F, 0xFF575C5C, 0xFF2E3030,
            0xFF56333E, 0xFF693E4B, 0xFF7A4958, 0xFF40262E,
            0xFF352B40, 0xFF41354F, 0xFF4C3E5C, 0xFF282030,
            0xFF352318, 0xFF412B1E, 0xFF4C3223, 0xFF281A12,
            0xFF35391D, 0xFF414624, 0xFF4C522A, 0xFF282B16,
            0xFF642A20, 0xFF7A3327, 0xFF8E3C2E, 0xFF4B2018,
            0xFF1A0F0B, 0xFF1F120D, 0xFF251610, 0xFF130B08,
            0xFF852122, 0xFFA3292A, 0xFFBD3031, 0xFF641919,
            0xFF682C44, 0xFF7F3653, 0xFF943F61, 0xFF4E2133,
            0xFF401114, 0xFF4F1519, 0xFF5C191D, 0xFF300D11,
            0xFF0F585E, 0xFF126C73, 0xFF167E86, 0xFF0B4246,
            0xFF286462, 0xFF327A78, 0xFF3A8E8C, 0xFF1E4B4A,
            0xFF3C1F2B, 0xFF4A2535, 0xFF562C3E, 0xFF2D1720,
            0xFF0E7F5D, 0xFF119B72, 0xFF14B485, 0xFF0A5F46,
            0xFF464646, 0xFF565656, 0xFF646464, 0xFF343434,
            0xFF987B67, 0xFFBA967E, 0xFFD8AF93, 0xFF726C5D,
            0xFF597569, 0xFF6D8F7D, 0xFF7FB796, 0xFF43584F
    };

    public static BufferedImage resizeAndEncodeImage(BufferedImage inputImage) {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();

        if (width != 128 || height != 128) {
            throw new IllegalArgumentException("Image must be 128x128");
        }

        // Create a 64x64 image (downsampled)
        BufferedImage resizedImage = new BufferedImage(64, 64, BufferedImage.TYPE_3BYTE_BGR);
        for (int y = 0; y < 64; y++) {
            for (int x = 0; x < 64; x++) {
                int rgb = inputImage.getRGB(x * 2, y * 2);
                resizedImage.setRGB(x, y, rgb);
            }
        }

        // Encode the 64x64 image into the 128x128 map
        BufferedImage mapImage = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < 64; y++) {
            for (int x = 0; x < 64; x++) {
                int rgb = resizedImage.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                int b1 = b & 0x7F;
                int msb1 = b >> 7;

                int b2 = g & 0x7F;
                int msb2 = g >> 7;

                int b3 = r & 0x7F;
                int msb3 = r >> 7;

                int b4 = (msb3 << 2) | (msb2 << 1) | msb1;

                mapImage.setRGB(x * 2, y * 2, mapColorPalette[b1]);
                mapImage.setRGB(x * 2 + 1, y * 2, mapColorPalette[b2]);
                mapImage.setRGB(x * 2, y * 2 + 1, mapColorPalette[b3]);
                mapImage.setRGB(x * 2 + 1, y * 2 + 1, mapColorPalette[b4]);
            }
        }

        return mapImage;
    }
}
