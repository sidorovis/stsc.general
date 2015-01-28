package stsc.news.feedzilla.downloader;

import graef.feedzillajava.Category;
import graef.feedzillajava.FeedZilla;
import graef.feedzillajava.Subcategory;

import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Logger;

class DownloadHelper {

	public static List<Category> getCategories(FeedZilla feed, Logger logger) {
		for (int amountOfTries = 0; amountOfTries < CallableArticlesDownload.TRIES_COUNT; ++amountOfTries) {
			try {
				return feed.getCategories();
			} catch (Exception e) {
				logger.error("Downloading categories throw exception: " + e.getMessage());
			}
			CallableArticlesDownload.pause(1000);
		}
		return Collections.emptyList();
	}

	public static List<Subcategory> getSubcategories(FeedZilla feed, Category category, Logger logger) {
		for (int amountOfTries = 0; amountOfTries < CallableArticlesDownload.TRIES_COUNT; ++amountOfTries) {
			try {
				return feed.getSubcategories(category);
			} catch (Exception e) {
				logger.debug("Downloading subcategories throw exception: " + e.getMessage());
			}
			CallableArticlesDownload.pause(700);
		}
		return Collections.emptyList();
	}
}
