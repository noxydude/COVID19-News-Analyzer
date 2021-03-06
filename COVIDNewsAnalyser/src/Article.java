import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.io.PrintWriter;
import java.time.LocalDateTime;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Article implements Comparable<Article> {

    private static String fileName = "saved_articles.txt";

    private String region;
    private String googleUrl;
    private String trueUrl;
    private String title;
    private LocalDateTime date;
    private String publisher;

    /**
     * Creates an Article class containing region, url, title, date, and publisher
     * @param region of the article
     * @param url    of the article
     * @param title  of the article
     * @param date   of the article
     * @param publisher of the article
     */
    public Article(String region, String url, String title, LocalDateTime date, String publisher) {
        this.region = region;
        this.googleUrl = url;
        this.title = title;
        this.date = date;
        this.publisher = publisher;
    }
    
    /**
     * @return fileName
     */
    public static String getFileName() {
        return fileName;
    }

    /**
     * @return the google url of the article
     */
    public String getGoogleUrl() {
        return googleUrl;
    }

    /**
     * We use the true url and not the google url.
     * @return the true url of the article
     */
    public String getTrueUrl() {
        Document doc;
        try {
            doc = Jsoup.connect(googleUrl).get();
            String wholeText = doc.text();
            String[] words = wholeText.split(" ");
            for (int i = 0; i < words.length; i++) {
                if (words[i].contains("Opening")) {
                    trueUrl = words[i + 1];
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (trueUrl == null) {
            throw new IllegalArgumentException("article's true url cannot be found!");
        }
        return trueUrl;
    }

    /**
     * @return title of article
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return date of article
     */
    public LocalDateTime getDate() {
        return date;
    }
    
    /**
     * @return publisher of article
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * @return a new articledocument class containing region, title, publisher, and date
     */
    public ArticleDocument getDocument() {
        return new ArticleDocument(getTrueUrl(), region, title, publisher, date);
    }

    /**
     * @return region of article
     */
    public String getRegion() {
        return region;
    }

    /**
     * Saves this article to our saved_articles.txt file. Avoids duplicates
     * @return  true if the article saved, false otherwise
     */
    public boolean saveArticle() {
        try {
            FileWriter writer = new FileWriter(fileName, true);
            PrintWriter out = new PrintWriter(writer);
            if (!articleAlreadySaved()) {
                out.append("ARTICLE:");
                out.append("\n");
                out.append(region);
                out.append("\n");
                out.append(googleUrl);
                out.append("\n");
                out.append(title);
                out.append("\n");
                out.append(date.toString());
                out.append("\n");
                out.append(publisher);
                out.append("\n");
                out.append("\n");
                out.flush();
                out.close();
                return true;
            }
            out.close();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lets us know if this article has already been saved
     * @return true if it has, false otherwise
     */
    private boolean articleAlreadySaved() {
        try {
            FileReader reader = new FileReader(fileName);
            BufferedReader bReader = new BufferedReader(reader);
            while (bReader.ready()) {
                String line = bReader.readLine();
                if (line.equals(googleUrl)) {
                    if (bReader.readLine().contains(title)) {
                        bReader.close();
                        return true;
                    }
                }
            }
            bReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Article)) {
            return false;
        }
        Article art = (Article) obj;
        return (this.googleUrl).equals(art.googleUrl);
    }

    @Override
    public String toString() {
        return "----\n" + this.title + "\n" + this.date + "\n" 
                + this.publisher + "\n" + this.googleUrl + "\n";
    }

    @Override
    public int compareTo(Article o) {
        int compared = o.date.compareTo(this.date);
        if (compared == 0) {
            if (o.googleUrl.equals(this.googleUrl)) {
                return 0;
            }
            return 1;
        }
        return compared;
    }

}
