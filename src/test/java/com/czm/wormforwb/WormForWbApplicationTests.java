package com.czm.wormforwb;

import com.czm.wormforwb.utils.FileUtils;
import com.czm.wormforwb.utils.PDFUtils;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.HorizontalAlignment;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;
import java.io.IOException;


@SpringBootTest
class WormForWbApplicationTests {

    @Test
    void test()  {
        String path = FileUtils.getLogDirPathToday() + "/test.pdf";
        System.out.println(path);
    }

    @Test
    void contextLoads() throws IOException {
        String dest = "/Users/slience/Desktop/test02/sample.pdf";
        PdfWriter writer = new PdfWriter(dest);

        // 2、Creating a PdfDocument
        PdfDocument pdfDoc = new PdfDocument(writer);

        // 3、Adding an empty page
        pdfDoc.addNewPage();

        // 4、Creating a Document
        Document document = new Document(pdfDoc);

        ImageData data = ImageDataFactory.create("/Users/slience/Desktop/pics/202203/7811cf6bly1h0huyd6b95j20de04nwel.jpg");
        Image image = new Image(data);
        image.setAutoScale(true);
        image.setHorizontalAlignment(HorizontalAlignment.CENTER);

        document.add(image);
        document.close();
    }

}
