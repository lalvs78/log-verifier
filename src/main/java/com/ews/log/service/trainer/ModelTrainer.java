package com.ews.log.service.trainer;

import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ModelTrainer {
    public static void main(String[] args) {
        try {
            // Load training data
            InputStreamFactory dataIn = new MarkableFileInputStreamFactory(new File("/work/training-data-update.txt"));
            ObjectStream<String> lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
            ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);

            // Training model parameters
            TrainingParameters params = new TrainingParameters();
            params.put(TrainingParameters.ITERATIONS_PARAM, 100);
            params.put(TrainingParameters.CUTOFF_PARAM, 0);

            // Train the model
            DoccatModel model = DocumentCategorizerME.train("en", sampleStream, params, new DoccatFactory());

            // Save the model to a file
            File outputFile = new File("/work/data-message-model.bin");
            try (OutputStream modelOut = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                model.serialize(modelOut);
            }

            System.out.println("Model training completed and saved to data-message-model.bin");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


