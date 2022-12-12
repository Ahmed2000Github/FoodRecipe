
// package controllers;

// import ai.djl.inference.*;
// import ai.djl.modality.*;
// import ai.djl.modality.cv.*;
// import ai.djl.modality.cv.util.*;
// import ai.djl.ndarray.*;
// import ai.djl.repository.zoo.*;
// import ai.djl.translate.*;
// import ai.djl.training.util.*;
// import ai.djl.util.*;
// import java.net.*;
// import java.nio.file.*;
// import java.util.*;

// public class TestML {
// public static void main(String[] args) {
// String imagePath = "https://resources.djl.ai/images/chest_xray.jpg";
// var image = ImageFactory.getInstance().fromUrl(imagePath);
// image.getWrappedImage();
// // Criteria<Image, Classifications> criteria = Criteria.builder()
// // .setTypes(Image.class, Classifications.class)
// // .optModelUrls(modelUrl)
// // .optTranslator(new MyTranslator())
// // .optProgress(new ProgressBar())
// // .build();
// // ZooModel model = criteria.loadModel();
// // Predictor<Image, Classifications> predictor = model.newPredictor();
// // Classifications classifications = predictor.predict(image);
// }
// }
