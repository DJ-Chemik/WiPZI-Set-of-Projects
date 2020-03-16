package pl.poznan.put;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OpenNLP {

    public static String LANG_DETECT_MODEL = "models/langdetect-183.bin";
    public static String TOKENIZER_MODEL = "models/en-token.bin";
    public static String DE_TOKENIZER_MODEL = "models/de-token.bin";
    public static String SENTENCE_MODEL = "models/en-sent.bin";
    public static String POS_MODEL = "models/en-pos-maxent.bin";
    public static String CHUNKER_MODEL = "models/en-chunker.bin";
    public static String LEMMATIZER_DICT = "models/en-lemmatizer.dict";
    public static String NAME_MODEL = "models/en-ner-person.bin";
    public static String ENTITY_XYZ_MODEL = "models/en-ner-xxx.bin";

    public static void main(String[] args) throws IOException {
        OpenNLP openNLP = new OpenNLP();
        openNLP.run();
    }

    public void run() throws IOException {

        languageDetection();
        tokenization();
        sentenceDetection();
        posTagging();
        lemmatization();
        stemming();
        chunking();
        nameFinding();
    }

    private void languageDetection() throws IOException {
        File modelFile = new File(LANG_DETECT_MODEL);
        LanguageDetectorModel model = new LanguageDetectorModel(modelFile);
        LanguageDetectorME languageDetectorME = new LanguageDetectorME(model);

        String text = "";
        text = "cats";
        text = "cats like milk";
        text = "Many cats like milk because in some ways it reminds them of their mother's milk.";
        text = "The two things are not really related. Many cats like milk because in some ways it reminds them of their mother's milk.";
        text = "The two things are not really related. Many cats like milk because in some ways it reminds them of their mother's milk. "
                + "It is rich in fat and protein. They like the taste. They like the consistency . "
                + "The issue as far as it being bad for them is the fact that cats often have difficulty digesting milk and so it may give them "
                + "digestive upset like diarrhea, bloating and gas. After all, cow's milk is meant for baby calves, not cats. "
                + "It is a fortunate quirk of nature that human digestive systems can also digest cow's milk. But humans and cats are not cows.";
//		text = "Many cats like milk because in some ways it reminds them of their mother's milk. Le lait n'est pas forc�ment mauvais pour les chats";
//		text = "Many cats like milk because in some ways it reminds them of their mother's milk. Le lait n'est pas forc�ment mauvais pour les chats. "
//		 + "Der Normalfall ist allerdings der, dass Salonl�wen Milch weder brauchen noch gut verdauen k�nnen.";

        Language language = languageDetectorME.predictLanguage(text);
        System.out.println("Predicted Language: " + language.getLang() + " with confidence: " + language.getConfidence());
    }

    private void tokenization() throws IOException {
        String text = "";

        text = "Since cats were venerated in ancient Egypt, they were commonly believed to have been domesticated there, "
                + "but there may have been instances of domestication as early as the Neolithic from around 9500 years ago (7500 BC).";
        text = "Since cats were venerated in ancient Egypt, they were commonly believed to have been domesticated there, "
                + "but there may have been instances of domestication as early as the Neolithic from around 9,500 years ago (7,500 BC).";
        text = "Since cats were venerated in ancient Egypt, they were commonly believed to have been domesticated there, "
                + "but there may have been instances of domestication as early as the Neolithic from around 9 500 years ago ( 7 500 BC).";

        //ENGLISH
        TokenizerModel tokenizerModel = new TokenizerModel(new File(TOKENIZER_MODEL));
        TokenizerME tokenizer = new TokenizerME(tokenizerModel);
        String[] outputTokens = tokenizer.tokenize(text);
        double[] probabilities = tokenizer.getTokenProbabilities();
        //GERMAN
//        TokenizerModel deTokenizerModel = new TokenizerModel(new File(DE_TOKENIZER_MODEL));
//        TokenizerME deTokenizer = new TokenizerME(deTokenizerModel);
//        String[] outputTokens = deTokenizer.tokenize(text);
//        double[] probabilities = deTokenizer.getTokenProbabilities();

        boolean wantDisplayTokens = false;
        if (wantDisplayTokens) {
            List<String> list = new ArrayList<>(Arrays.asList(outputTokens));
            for (int i = 0; i < list.size(); i++) {
                System.out.println(list.get(i) + " <<" + probabilities[i] + ">>");
            }
        }


    }

    private void sentenceDetection() throws IOException {
        String text = "";
        text = "Hi. How are you? Welcome to pl.poznan.put.OpenNLP. "
                + "We provide multiple built-in methods for Natural Language Processing.";
        text = "Hi. How are you?! Welcome to pl.poznan.put.OpenNLP? "
                + "We provide multiple built-in methods for Natural Language Processing.";
        text = "Hi. How are you? Welcome to pl.poznan.put.OpenNLP.?? "
                + "We provide multiple . built-in methods for Natural Language Processing.";
//		text = "The interrobang, also known as the interabang (often represented by ?! or !?), "
//				+ "is a nonstandard punctuation mark used in various written languages. "
//				+ "It is intended to combine the functions of the question mark (?), or interrogative point, "
//				+ "and the exclamation mark (!), or exclamation point, known in the jargon of printers and programmers as a \"bang\". ";

        SentenceModel sentenceModel = new SentenceModel(new File(SENTENCE_MODEL));
        SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
        List<String> sentences = new ArrayList<>(Arrays.asList(detector.sentDetect(text)));
        double[] probabilities = detector.getSentenceProbabilities();

        boolean printSentences = false;
        if (printSentences) {
            for (int i = 0; i < sentences.size(); i++) {
                System.out.println(sentences.get(i) + "    <<" + probabilities[i] + ">>");
            }
        }

    }

    private void posTagging() throws IOException {
        String[] sentence = new String[0];
        sentence = new String[]{"Cats", "like", "milk"};
        sentence = new String[]{"Cat", "is", "white", "like", "milk"};
        sentence = new String[]{"Hi", "How", "are", "you", "Welcome", "to", "pl.poznan.put.OpenNLP", "We", "provide", "multiple",
                "built-in", "methods", "for", "Natural", "Language", "Processing"};
//		sentence = new String[] { "She", "put", "the", "big", "knives", "on", "the", "table" };

        POSModel posModel = new POSModel(new File(POS_MODEL));
        POSTaggerME tagger = new POSTaggerME(posModel);
        List<String> tags = new ArrayList<>(Arrays.asList(tagger.tag(sentence)));

        boolean display = false;
        if (display) {
            for (int i = 0; i < tags.size(); i++) {
                System.out.println(sentence[i] + " -> " + tags.get(i));
            }
        }

    }

    private void lemmatization() throws IOException {
        String[] text = new String[0];
        text = new String[]{"Hi", "How", "are", "you", "Welcome", "to", "pl.poznan.put.OpenNLP", "We", "provide", "multiple",
                "built-in", "methods", "for", "Natural", "Language", "Processing"};
        //text = new String[]{"hsfadbhdsfh", "kapiszon", "qwerty"};
        String[] tags = new String[0];
        tags = new String[]{"NNP", "WRB", "VBP", "PRP", "VB", "TO", "VB", "PRP", "VB", "JJ", "JJ", "NNS", "IN", "JJ",
                "NN", "VBG"};

        DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(new File(LEMMATIZER_DICT));

        String[] lemats = lemmatizer.lemmatize(text,tags);
        boolean display = false;
        if (display){
            for (String s : lemats){
                System.out.println(s);
            }
        }

    }

    private void stemming() {
        String[] sentence = new String[0];
        sentence = new String[]{"Hi", "How", "are", "you", "Welcome", "to", "pl.poznan.put.OpenNLP", "We", "provide", "multiple",
                "built-in", "methods", "for", "Natural", "Language", "Processing"};
        //sentence = new String[]{"hsfadbhdsfh", "kapiszon", "qwerty"};

        PorterStemmer stemmer = new PorterStemmer();

        boolean display = false;
        if (display){
            for (String sen : sentence) {
                String s = stemmer.stem(sen);
                System.out.println(s);
            }
        }


    }

    private void chunking() throws IOException {
        String[] sentence = new String[0];
        sentence = new String[]{"She", "put", "the", "big", "knives", "on", "the", "table"};

        String[] tags = new String[0];
        tags = new String[]{"PRP", "VBD", "DT", "JJ", "NNS", "IN", "DT", "NN"};

        ChunkerModel chunkerModel = new ChunkerModel(new File(CHUNKER_MODEL));
        ChunkerME chunkerME = new ChunkerME(chunkerModel);
        List<String> chunks = new ArrayList<>(Arrays.asList(chunkerME.chunk(sentence, tags)));

        boolean display = false;
        if (display){
            for (int i = 0; i < chunks.size(); i++) {
                System.out.println(chunks.get(i));
            }
        }

    }

    private void nameFinding() throws IOException {
        String text = "he idea of using computers to search for relevant pieces of information was popularized in the article "
                + "As We May Think by Vannevar Bush in 1945. It would appear that Bush was inspired by patents "
                + "for a 'statistical machine' - filed by Emanuel Goldberg in the 1920s and '30s - that searched for documents stored on film. "
                + "The first description of a computer searching for information was described by Holmstrom in 1948, "
                + "detailing an early mention of the Univac computer. Automated information retrieval systems were introduced in the 1950s: "
                + "one even featured in the 1957 romantic comedy, Desk Set. In the 1960s, the first large information retrieval research group "
                + "was formed by Gerard Salton at Cornell. By the 1970s several different retrieval techniques had been shown to perform "
                + "well on small text corpora such as the Cranfield collection (several thousand documents). Large-scale retrieval systems, "
                + "such as the Lockheed Dialog system, came into use early in the 1970s.";

        TokenizerModel tokenizerModel = new TokenizerModel(new File(TOKENIZER_MODEL));
        TokenizerME tokenizer = new TokenizerME(tokenizerModel);
        String[] outputTokens = tokenizer.tokenize(text);
        double[] probabilities = tokenizer.getTokenProbabilities();

        TokenNameFinderModel model = new TokenNameFinderModel(new File(NAME_MODEL));
        model = new TokenNameFinderModel(new File(ENTITY_XYZ_MODEL));
        NameFinderME nameFinder = new NameFinderME(model);
        Span[] spans = nameFinder.find(outputTokens);

        boolean display = false;
        if (display){
            for (Span s:spans) {
                System.out.println(s.getStart() + ": " + outputTokens[s.getStart()]);
            }
        }

    }

}
