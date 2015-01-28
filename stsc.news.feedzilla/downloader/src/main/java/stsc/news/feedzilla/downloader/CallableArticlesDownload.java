package stsc.news.feedzilla.downloader;

import graef.feedzillajava.Article;
import graef.feedzillajava.Articles;
import graef.feedzillajava.Category;
import graef.feedzillajava.FeedZilla;
import graef.feedzillajava.Subcategory;

import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;
import org.joda.time.DateTime;

class CallableArticlesDownload implements Callable<Optional<List<Article>>> {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private static Logger callableLogger = LogManager.getLogger(CallableArticlesDownload.class);

	public static final int TRIES_COUNT = 5;
	public static final long PAUSE_SLEEP_TIME = 100;

	private final FeedZilla feed;
	private final Category category;
	private final Subcategory subcategory;
	private final int amountOfArticlesPerRequest;
	private final DateTime startOfDay;

	public CallableArticlesDownload(FeedZilla feed, Category category, Subcategory subcategory, int amountOfArticlesPerRequest,
			DateTime startOfDay) {
		super();
		this.feed = feed;
		this.category = category;
		this.subcategory = subcategory;
		this.amountOfArticlesPerRequest = amountOfArticlesPerRequest;
		this.startOfDay = startOfDay;
	}

	@Override
	public Optional<List<Article>> call() throws Exception {
		final long startArticlesLoadTime = System.currentTimeMillis();
		Exception exceptionToReturn = new Exception();
		for (int amountOfTries = 0; amountOfTries < TRIES_COUNT; ++amountOfTries) {
			try {
				final Articles articles = feed.query().category(category.getId()).subcategory(subcategory.getId()).since(startOfDay)
						.count(amountOfArticlesPerRequest).articles();
				final Optional<List<Article>> result = Optional.of(articles.getArticles());
				final long endArticlesLoadTime = System.currentTimeMillis();
				callableLogger.trace("articles load took: " + (endArticlesLoadTime - startArticlesLoadTime) + " ms");
				return result;
			} catch (Exception e) {
				exceptionToReturn = e;
			}
			pause();
		}
		final long endArticlesLoadTime = System.currentTimeMillis();
		callableLogger.trace("no articles and it took: " + (endArticlesLoadTime - startArticlesLoadTime) + " ms; "
				+ exceptionToReturn.getMessage());
		return Optional.empty();
	}

	public static void pause() {
		pause(PAUSE_SLEEP_TIME);
	}

	public static void pause(long timeInMillis) {
		try {
			Thread.sleep(timeInMillis);
		} catch (Exception e) {
		}
	}

}
