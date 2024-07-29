package utils;
import java.util.Comparator;

import beans.Image;

public class ImageComparator implements Comparator<Image> {

    public int compare(Image i1, Image i2) {
        if (i1.getCreationDate().compareTo(i2.getCreationDate()) < 0) {
            return -1;
        } else if (i1.getCreationDate().compareTo(i2.getCreationDate()) > 0) {
            return 1;
        }
        return 0;
    }
}