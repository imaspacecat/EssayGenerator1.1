import org.fastily.jwiki.core.*;

import java.util.ArrayList;
import java.util.regex.*;

class Main
{
    public static void main(String[] args){

        String articleName = "Cookie";
        Wiki wiki = new Wiki.Builder().build();
        String content = wiki.getPageText(articleName);


        /**
         iterate over all indexes less then the split array size of the content [i]
         set the categories' title param to the capture group of the regex
         set the content param to the split(regex) array's index [i]
         **/
        int size = 50;
        ArrayList<Categories> categories = new ArrayList<Categories>(size);

        String[] splitArray = content.split("==.*?==");

        Pattern pattern = Pattern.compile("==(.*?)==");
        Matcher matcher = pattern.matcher(content);

        /**
         to-do:
         - make it not include ==citations==, ==further reading==, ==external links==, ==general sources==...
         - make sub categories connected somehow to larger categories so when generating the essay it can add those as smaller parts of the main body paragraph. E.g. Maintenance and Repair -> Maintenance, Repair
         **/
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        String[] titles = new String[count];

        matcher.reset();

        int index = 0;
        while (matcher.find()) {
            titles[index] = matcher.group();
            index++;
        }

        categories.add(0, (new Categories(articleName, splitArray[0])));
        for (int i = 1; i < splitArray.length; i++) {
            categories.add(i, (new Categories(titles[i-1], splitArray[i])));
        }


        for (int i = 0; i < categories.size(); i++) {

            categories.set(i, new Categories(categories.get(i).getTitle(), categories.get(i).getContent().replaceAll("\\[\\[File.*]]", "")));
            categories.set(i, new Categories(categories.get(i).getTitle(), clean("\\[\\[[^\\[\\[]*?([^|]*?)\\]\\]", categories.get(i).getContent()+"[[|]]")));
            categories.set(i, new Categories(categories.get(i).getTitle(), clean("'''(.*?)'''", categories.get(i).getContent()+"''''''")));
            categories.set(i, new Categories(categories.get(i).getTitle(), clean("''(.*?)''", categories.get(i).getContent()+"''''")));
            categories.set(i, new Categories(categories.get(i).getTitle(), categories.get(i).getContent().replaceAll("(?s)<ref.*?</ref>", "")));
            categories.set(i, new Categories(categories.get(i).getTitle(), categories.get(i).getContent().replaceAll("(?s)<ref.*?/>", "")));
            categories.set(i, new Categories(categories.get(i).getTitle(), categories.get(i).getContent().replaceAll("(?s)<gallery.*?</gallery>", "")));

            categories.set(i, new Categories(categories.get(i).getTitle(), categories.get(i).getContent().replaceAll("\\{\\{.*?}}", "")));


            System.out.println("index: " + i + ", title: " + categories.get(i).getTitle() + "\ncontent: \n" + categories.get(i).getContent());
        }

    }

    static String clean(String regex, String content){

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        StringBuffer stringBuffer = new StringBuffer();

        while(matcher.find()){
            matcher.appendReplacement(stringBuffer, "$1");
        }
        return stringBuffer.toString();
    }


}