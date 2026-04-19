package com.example.backend_sis.service;

import com.example.backend_sis.exception.BusinessException;
import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintImageOptions;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class SourceAfisService {

    @Value("${huella.sourceafis.match-threshold:40}")
    private double matchThreshold;

    public byte[] decodeRawBase64(String rawImageBase64) {
        try {
            return Base64.getDecoder().decode(rawImageBase64);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("La imagen RAW de huella en Base64 no es válida.");
        }
    }

    public FingerprintTemplate buildTemplate(byte[] rawImage, int width, int height, int dpi) {
        validateImage(rawImage, width, height, dpi);

        FingerprintImageOptions options = new FingerprintImageOptions().dpi(dpi);
        FingerprintImage image = new FingerprintImage(width, height, rawImage, options);
        return new FingerprintTemplate(image);
    }

    public byte[] serializeTemplate(FingerprintTemplate template) {
        return template.toByteArray();
    }

    public FingerprintTemplate deserializeTemplate(byte[] serializedTemplate) {
        if (serializedTemplate == null || serializedTemplate.length == 0) {
            throw new BusinessException("El template biométrico almacenado es inválido.");
        }
        return new FingerprintTemplate(serializedTemplate);
    }

    public double match(FingerprintTemplate probe, FingerprintTemplate candidate) {
        return new FingerprintMatcher(probe).match(candidate);
    }

    public double getMatchThreshold() {
        return matchThreshold;
    }

    private void validateImage(byte[] rawImage, int width, int height, int dpi) {
        if (rawImage == null || rawImage.length == 0) {
            throw new BusinessException("La imagen RAW de huella está vacía.");
        }
        if (width <= 0 || height <= 0) {
            throw new BusinessException("Las dimensiones de la imagen de huella son inválidas.");
        }
        if (dpi <= 0) {
            throw new BusinessException("El DPI informado para la huella es inválido.");
        }
        int expectedLength = width * height;
        if (rawImage.length != expectedLength) {
            throw new BusinessException("La longitud de la imagen RAW no coincide con width x height.");
        }
    }
}
