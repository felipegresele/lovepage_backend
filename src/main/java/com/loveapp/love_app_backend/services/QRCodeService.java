package com.loveapp.love_app_backend.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.loveapp.love_app_backend.modal.types.QrCodeFrame;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

@Service
public class QRCodeService {

    private static final Map<String, String> FRAME_PATHS = Map.of(
            "ESCANEIE", "static/images/escaneie-e-se-surprenda-sem-qr.png",
            "JUNTOS",   "static/images/juntos-para-sempre-sem-qr.png",
            "SPOTIFY",  "static/images/spotify-sem-qr.png",
            "SURPRESA", "static/images/surpresa-para-vc-sem-qr.png",
            "CARTA", "static/images/carta-sem-qr.png"
    );

    // Formato: { x, y, largura, altura }
    // ESCANEIE e JUNTOS: QR 200x200 centralizado na área branca
    // SURPRESA: QR 300x300 centralizado na área branca (área maior comporta)
    private static final Map<String, int[]> FRAME_QR_BOUNDS = Map.of(
            "ESCANEIE", new int[]{149, 157, 200, 200},
            "JUNTOS", new int[]{230, 220, 200, 200},
            "SPOTIFY",  new int[]{150, 150, 200, 200},
            "SURPRESA", new int[]{ 99, 103, 300, 300},
            "CARTA", new int[]{ 140, 140, 200, 200}
    );

    public byte[] generate(String url) throws Exception {
        return generateQRBytes(url, 200, 200);
    }

    public byte[] generateWithFrame(String url, QrCodeFrame frame) throws Exception {
        if (frame == null || frame == QrCodeFrame.NONE) {
            return generate(url);
        }

        InputStream frameStream;
        try {
            frameStream = new ClassPathResource(FRAME_PATHS.get(frame.name())).getInputStream();
        } catch (Exception e) {
            return generate(url);
        }

        BufferedImage frameImage = ImageIO.read(frameStream);
        if (frameImage == null) {
            return generate(url);
        }

        int[] bounds = FRAME_QR_BOUNDS.get(frame.name());

        byte[] qrBytes = generateQRBytes(url, bounds[2], bounds[3]);
        BufferedImage qrImage = ImageIO.read(new ByteArrayInputStream(qrBytes));

        BufferedImage result = new BufferedImage(
                frameImage.getWidth(),
                frameImage.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = result.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(frameImage, 0, 0, null);
        g.drawImage(qrImage, bounds[0], bounds[1], bounds[2], bounds[3], null);
        g.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(result, "PNG", out);
        return out.toByteArray();
    }

    // QR vermelho escuro sobre fundo branco
    private byte[] generateQRBytes(String url, int width, int height) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(url, BarcodeFormat.QR_CODE, width, height);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0xCC0000 : 0xFFFFFF);
            }
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", stream);
        return stream.toByteArray();
    }
}