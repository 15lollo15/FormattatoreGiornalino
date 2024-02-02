package org.example;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SplitImagesGenerator {
    private static SplitImagesGenerator splitImagesGenerator;
    private SplitImagesGenerator() {}

    public static SplitImagesGenerator getInstance() {
        if (splitImagesGenerator == null)
            splitImagesGenerator = new SplitImagesGenerator();
        return splitImagesGenerator;
    }

    public List<BufferedImage> generateSplitImages(List<BufferedImage> images) {
        List<BufferedImage> splitImages = new ArrayList<>();
        for (BufferedImage img : images) {
            BufferedImage[] splits = splitInTwo(img);
            splitImages.add(splits[0]);
            splitImages.add(splits[1]);
        }

        BufferedImage lastPage = splitImages.remove(0);
        splitImages.add(lastPage);

        return splitImages;
    }

    private BufferedImage[] splitInTwo(BufferedImage img) {
        int width = img.getWidth() / 2;
        BufferedImage left = img.getSubimage(0, 0, width, img.getHeight());
        BufferedImage right = img.getSubimage(width, 0, img.getWidth() - width, img.getHeight());

        return new BufferedImage[] {left, right};
    }

}
