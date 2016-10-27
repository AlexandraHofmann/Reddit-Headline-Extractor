import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



/**
 * Class that reads the Reddit RSS and saves them in one file per year.
 */
public class FeedReader extends Thread{


	public static void main(String[] args){
		
		String fileToPath = "C:/Users/D060249/Desktop/TMP/newsfeed.csv";
		//C:/Users/Alina/Desktop/Data Mining/Projekt/newsFeed7.csv
		

		// long startTime = 1420070400; // 01.01.2015
		// long endTime = 1421107199; // 12.01.2015
		
		// long startTime2 = 1422835199;
		// long endTime2 = 1423785599;
		//String fileToPath2 = "C:/Users/Alina/Desktop/Data Mining/Projekt/newsFeed.csv";
		
		
		
		Thread t1 = new Thread(new feedGetter("2015-01-01", "2015-01-12", fileToPath), "Thread 1");
	    //	Thread t1 = new Thread(new feedGetter(new Date(2015, 1, 1), new Date(2015, 1, 12), fileToPath), "Thread 1");
		// todo: 10 threads per year
		// todo: add Alexandras 
		
		t1.start();
		//t2.start();
	}
	
	/**
	 * 
	 * The Runnable that gets the reddit feed.
	 *
	 */
	private static class feedGetter implements Runnable {
		
		private	long timestampBegin;
		private long timestampEnd;
		private long endTime;
		private BufferedReader rssReader; // reader for RSS files
		private BufferedWriter csvFileWriter; // writer that writes into the specified csv file
		private URL url; // URL object for getting the data
		private String generatedUrl; // the current concatenated URL
		private String pathToFile; // path to the file in which the reader writes the headlines
		public ArrayList<String> urlsThatDidNotWork; // contains the URLs that did not work
		private SimpleDateFormat sdf; 
		// http://docs.aws.amazon.com/cloudsearch/latest/developerguide/searching-dates.html
		
		/**
		 * Constructor.
		 * @param startDate Date from which onwards the reddit feed is extracted.
		 * @param endDate Date up to which the reddit feed is extracted. The data will be extracted for as well.
		 * @param pathToFile The file where you want your CSV File to be stored in.
		 */
		public feedGetter (String startDate, String endDate, String pathToFile){
			
			// converting the input into UTC time in milliseconds
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz"); // converter for easier input

			try {
			this.timestampBegin = sdf.parse(startDate + " 00:00:00.000 UTC").getTime();
			this.endTime = sdf.parse(endDate + " 23:59:59.999 UTC").getTime();
			} catch (ParseException e) {
				System.out.println("There was a problem parsing startTime or endTime.");
			}

			this.timestampEnd =  this.timestampBegin + (24*60*60) - 1; //seconds per day
			this.pathToFile = pathToFile;
			this.urlsThatDidNotWork = new ArrayList<String>();
			
		}
					
			// do the following coding in a loop so that the URL changes day by day.
			// write the data in a file (this will be used for the data input in RapidMiner)
			
		@Override	
		public void run(){	
		
			int i = 0;
			try {
				csvFileWriter = new BufferedWriter(new FileWriter(new File(this.pathToFile)));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println(Thread.currentThread().getName() + ": The provided path for the file does not work.");
				e1.printStackTrace();
				return;
			}
			
			do {
//				try {
//					Thread.sleep(1);
//				} catch (InterruptedException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
				String start = Long.toString(this.timestampBegin);
				String end = Long.toString(this.timestampEnd);
				InputStream inputStream;
	//			generatedUrl = "https://www.reddit.com/r/worldnews/search/.rss?q=timestamp:1420070400..1420156799&sort=top&restrict_sr=on&limit=5&syntax=cloudsearch";
				generatedUrl = "https://www.reddit.com/r/worldnews/search/.rss?q=timestamp:"
									+ start + ".." 
									+ end 
									+"&sort=top&restrict_sr=on&limit=5&syntax=cloudsearch";
				System.out.println(Thread.currentThread().getName() + " day " + i);
				try {
					url = new URL(generatedUrl);
					System.out.println(Thread.currentThread().getName() + " " + url);
					
					int j = 0;
					while (j <= 20) {
						try {
						j++;
						inputStream = url.openStream();
						
						
					
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					rssReader = new BufferedReader(inputStreamReader);
					
					// very important to do some preprocessing here (XML parser)...
						
					String readLineFromReddit;
					while(((readLineFromReddit = rssReader.readLine()) != null)){	
						csvFileWriter.write(readLineFromReddit);
					}
					
					System.out.println(Thread.currentThread().getName() + " DONE");
					break;
						} catch(Exception e){
							System.out.println(Thread.currentThread().getName() + " Problem. Retry.");
							if (j == 20){
								urlsThatDidNotWork.add("day: " + i + "url: " + generatedUrl);
							}
							
						}
					}
		
					
				} catch (MalformedURLException e) {
					// TODO error handling
					System.out.println("Malformed URL");
					e.printStackTrace();
					
				} catch (IOException e){
					// TODO error handling
					System.out.println("IOException");
					
				} finally {
					// close all streams
					
					try {
					 	rssReader.close();
								
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				i++;
				timestampBegin = timestampEnd + 1;
				timestampEnd = timestampBegin + (24*60*60) - 1;
			} while (timestampEnd < endTime);
			
			System.out.println(Thread.currentThread().getName() + " URLs that did not work: ");
			
			if (urlsThatDidNotWork.isEmpty())
			{
				System.out.println("none");
			} else {
				for(String s : urlsThatDidNotWork){
					System.out.println(s);
				}
			}
			
			try {
				csvFileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}
}
