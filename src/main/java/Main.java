import org.fastily.jwiki.core.*;

import java.util.ArrayList;
import java.util.regex.*;
import java.util.Random;
import java.util.Scanner;

class Main
{
    private int var;

    public static void main(String[] args){

        System.out.println("What topic do you want your essay about?");
        Scanner scanner = new Scanner(System.in);

        String initialArticle = scanner.nextLine();

//        String articleName = "Water";
        Wiki wiki = new Wiki.Builder().build();

        String[] articleList = wiki.search(initialArticle, 6).toString().split(",");

        for(int i = 0; i < articleList.length; i++){
            String art = articleList[i];

            System.out.println(art);
            art = art.replaceAll("\\[", "");
            articleList[i] = art.replaceAll("]", "");
        }

        System.out.println("Select an article (enter a number between 1 and 6)");

        int articleIndex = scanner.nextInt();

        String articleName = articleList[articleIndex-1];

        String content = wiki.getPageText(articleName);


        //====x====
        //==x==
        /*
         iterate over all indexes less than the split array size of the content [i]
         set the categories' title param to the capture group of the regex
         set the content param to the split(regex) array's index [i]
         */
        int size = 50;
        ArrayList<Categories> categories = new ArrayList<>(size);

        String[] splitArray = content.split("====.*?====|===.*?===|==.*?==");

        Pattern pattern = Pattern.compile("====(.*?)====|===(.*?)===|==(.*?)==");
        Matcher matcher = pattern.matcher(content);

        /*
         to-do:
         - make sub categories connected somehow to larger categories so when generating the essay it can add those as smaller parts of the main body paragraph. E.g. Maintenance and Repair -> Maintenance, Repair
         - fix extra pair of = when cleaning categories (because of sub categories)
         - fix ====x==== subcategories being removed
         ==x== ===x=== ====x====
         */
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        String[] titles = new String[count];

        matcher.reset();

        int index = 0;
        while (matcher.find()) {
            String str = matcher.group();
            str = str.replace("=", "");
            titles[index++] = str.trim();
        }

        categories.add(0, (new Categories(articleName, splitArray[0])));
        for (int i = 1; i < splitArray.length; i++) {
            categories.add(i, (new Categories(titles[i-1], splitArray[i])));
        }

        ArrayList<Categories> cleanCategories = new ArrayList<>(size);


        for(int i = 0; i < categories.size(); i++){
            String title = categories.get(i).getTitle().toLowerCase();
            if(
                    title.equals("citations") ||
                    title.equals("further reading") ||
                    title.equals("external links") ||
                    title.equals("general sources") ||
                    title.equals("notes") ||
                    title.equals("references") ||
                    title.equals("gallery") ||
                    title.equals("see also"))
            {
                continue;
            }


            cleanCategories.add(new Categories(title, categories.get(i).getContent()));


        }

    // ==x== ===x=== ====x====
        for (int i = 0; i < cleanCategories.size(); i++) {
            String res = cleanCategories.get(i).getContent();
            res = res.replaceAll("\\[\\[File.*]]", "");
            res = clean("\\[\\[[^\\[\\[]*?([^|]*?)\\]\\]", res+"[[|]]");
            res = clean("'''(.*?)'''", res+"''''''");
            res = clean("''(.*?)''", res+"''''");
            res = res.replaceAll("(?s)<ref.*?</ref>", "");
            res = res.replaceAll("(?s)<ref.*?/>", "");
            res = res.replaceAll("(?s)<gallery.*?</gallery>", "");
            res = res.replaceAll("(?s)\\{\\{.*?}}", "");
            res = res.replaceAll("(?s)<!--.*-->", "");

            cleanCategories.set(i, new Categories(cleanCategories.get(i).getTitle(), res));

//            System.out.println("title " + i + " is " + cleanCategories.get(i).getTitle());

        }

        //put together essay
        Random random = new Random();
        int randIndex = random.nextInt(cleanCategories.size() - 1);
        String essay = topicSentence(articleName, cleanCategories.get(randIndex).getTitle().toLowerCase()) + cleanCategories.get(randIndex).getContent();

        randIndex = random.nextInt(cleanCategories.size() - 1);
        essay = essay + topicSentence(articleName, cleanCategories.get(randIndex).getTitle().toLowerCase()) + cleanCategories.get(randIndex).getContent();

        randIndex = random.nextInt(cleanCategories.size() - 1);
        essay = essay + topicSentence(articleName, cleanCategories.get(randIndex).getTitle().toLowerCase()) + cleanCategories.get(randIndex).getContent() + conclusionSentence(articleName);

        essay = essay.replace("\n\n", "\n");

        System.out.println(essay);


    }

    private static String clean(String regex, String content){

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        StringBuilder stringBuffer = new StringBuilder();

        while(matcher.find()){
            matcher.appendReplacement(stringBuffer, "$1");
        }
        return stringBuffer.toString();
    }

    //add subcategories
    private static String topicSentence(String topic, String title){
        String[] topicArray = {
                "The " + title + " of " + topic.toLowerCase() + " is a very interesting topic. ",
                "There are many cool things to know about the " + title + " of " + topic.toLowerCase() + ". ",
                topic + " has had a very rich and interesting history. ",
                "Not many people truly know about the " + title + " of " + topic.toLowerCase() + ". ",
                "I love the " + title + " of " + topic.toLowerCase() + " as it is very wonderful. ",
                "Have you heard about the " + title + " of " + topic.toLowerCase() + "? ",
                "Have you ever thought about the story of the " + title + " of " + topic.toLowerCase() + "? "
        };

        if(topic.toLowerCase().equalsIgnoreCase(title)){
            topicArray = new String[]{
                    topic + " is a very interesting topic. ",
                    "There are many cool things to know about the " + title + ". ",
                    topic + " has had a very rich and interesting history. ",
                    "Not many people truly know about " + title + ". ",
                    "I love the " + title + " as it is very wonderful. ",
                    "Have you heard about " + title + "? ",
                    "Have you ever thought about the story of " + title + "? "
            };
        }

        Random random = new Random();
        int randIndex = random.nextInt(topicArray.length-1);
        return topicArray[randIndex];
    }

    private static String conclusionSentence(String topic){

        String[] conclusionArray = {
                " Now you know all about " + topic.toLowerCase() + ".",
                " As you can see, " + topic.toLowerCase() + " has a wonderful history.",
                " I hope that now, when you think of " + topic.toLowerCase() + ", you understand it better.",
                " And this is why I believe" + topic.toLowerCase() + "is so amazing.",
                " I hope that by reading this, you have developed a liking for " + topic.toLowerCase() + ".",
                topic + " is amazing and I hope you can see that now."
        };

        Random random = new Random();
        int randIndex = random.nextInt(conclusionArray.length-1);
        return conclusionArray[randIndex];
    }


}