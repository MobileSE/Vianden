BUG REPORT:

BUG 1, 03 August 2016, Luxembourg, KUI LIU
When the start year is earlier than 1998, the tool will throw an exception in thread "main" java.lang.NullPointerException
	at org.vianden.SearchEngine.getPapers(SearchEngine.java:187)
	at org.vianden.SearchEngine.search(SearchEngine.java:80)
	at org.vianden.SearchEngineTest.main(SearchEngineTest.java:12
The tool works well when the year is equal to or later than 1998.

BUG 2:


BUG 3:
The url "http://dl.com.org/citation.cfm?doid=2543920" is unavailable.
This bug comes from frequently accessing to the website of ACM.
Exception in thread "main" org.jsoup.HttpStatusException: HTTP error fetching URL. Status=403, URL=http://dl.acm.org/citation.cfm?doid=2543920
	at org.jsoup.helper.HttpConnection$Response.execute(HttpConnection.java:590)
	at org.jsoup.helper.HttpConnection$Response.execute(HttpConnection.java:587)
	at org.jsoup.helper.HttpConnection$Response.execute(HttpConnection.java:540)
	at org.jsoup.helper.HttpConnection.execute(HttpConnection.java:227)
	at org.jsoup.helper.HttpConnection.get(HttpConnection.java:216)
	at org.vianden.crawler.AbstractCrawler.commonCrawl(AbstractCrawler.java:53)
	at org.vianden.crawler.AbstractCrawler.<init>(AbstractCrawler.java:47)
	at org.vianden.crawler.ACMCrawler.<init>(ACMCrawler.java:17)
	at org.vianden.SearchEngine.refine(SearchEngine.java:94)
	at org.vianden.SearchEngineTest.main(SearchEngineTest.java:26)
