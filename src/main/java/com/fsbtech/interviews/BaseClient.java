package com.fsbtech.interviews;

import com.fsbtech.interviews.entities.Event;
import com.fsbtech.interviews.entities.MarketRefType;
import com.fsbtech.interviews.entities.SubCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class BaseClient implements Client {

    final static Logger logger = LogManager.getLogger(BaseClient.class);

    protected static HashMap<Integer, Event> events = new HashMap<Integer, Event>();
    protected static HashMap<Integer, Event> eventsArchive = new HashMap<Integer, Event>();

    private static ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

    // keep event id counter;
    public static int eventId = 0;

    /**
     * Safely increments the next event id
     */
    public synchronized int getNextEventId() {
        eventId = eventId + 1;
        logger.info("eventId is " + eventId);
        return eventId;
    }
    /**
     * allows reinitialise of next event id - for unit testing really
     */
    public synchronized void resetEventId() {
        eventId = 0;
        logger.info("eventId is " + eventId);
    }

    /**
     * get Event given id
     * @param id event Id
     * @return Event event
     */
    public Event getEvent(Integer id) {
        Event event = null;
        if (events.containsKey(id)) {
            event = events.get(id);
            logger.info("Event <{}> for id:{}", event.getName(), id);
        } else {
            logger.info("Event not found for id:{}", id);
        }
        return event;
    }

    /**
     * add Event to the events map
     * @param event - Event object representing a new Event to add to the repository.
     */
    public void addEvent(Event event) {
        System.out.println(events.size() + " events before adding event " + event.getId() + ":" + event.getName());
        reentrantReadWriteLock.writeLock().lock();
        try {
            events.put(event.getId(), event);
        } finally {
            reentrantReadWriteLock.writeLock().unlock();
        }
        logger.info("Now there are {} events after adding event {}:{}", events.size(), event.getId(), event.getName());
    }

    /**
     * update Event as completed
     * @param event
     * @return Event
     */
    public Event createEventAsCompleted(Event event) {
        return new Event(event.getId(), event.getName(), event.getSubCategory(),
                event.getMarketRefTypes(), Boolean.TRUE);
    }

    /**
     * update Event with marketRefTypes
     * @param event
     * @param marketRefTypes
     * @return Event
     */
    public Event createEventWithNewMarketRefTypes(Event event, Collection<MarketRefType> marketRefTypes) {
        return new Event(event.getId(), event.getName(), event.getSubCategory(),
                marketRefTypes, event.getCompleted());
    }

    /**
     * perform event completion:
     *   - remove event from events
     *   - add event into events Archive
     * @param id - Identifier for event to be marked as completed.
     */
    public void eventCompleted(Integer id) {
        // get event for given id
        Event event = events.get(id);
        Event eventCompleted = new Event(event.getId(), event.getName(), event.getSubCategory(),
                event.getMarketRefTypes(), Boolean.TRUE);

        if (null != event) {

            // remove from future Events
            reentrantReadWriteLock.writeLock().lock();
            try {
                events.remove(id);
                logger.info("event {}:{{} removed from events", id, event.getName());
                // add into archive - assume not archived before
                eventsArchive.put(id, eventCompleted);
                logger.info("event {}:{{} added to events Archive", id, event.getName());
            } finally {
                reentrantReadWriteLock.writeLock().unlock();
            }
        } else {
            logger.info("event not found for id {}", id);
        }
    }

    /**
     * attach marketRefType to event
     * get event for given id
     * if event found and event doesn't already contain marketRefType
     *  then update event and replace in events map
     *
     * @param id - Identifier for event to add marketRefType to.
     * @param marketRefType - Market supported by Event.
     */
    public void attachMarketRefTypeToEvent(Integer id, MarketRefType marketRefType) {
        Event event = events.get(id);
        if (null != event && !event.getMarketRefTypes().contains(marketRefType)) {
            // create new list of marketRefTypes
            // append existing values
            // append given marketRefType
            ArrayList<MarketRefType> marketRefTypes = new ArrayList<MarketRefType>();
            event.getMarketRefTypes().stream().forEach(e -> marketRefTypes.add(e));
            marketRefTypes.add(marketRefType);
            Event eventNew = createEventWithNewMarketRefTypes(event, marketRefTypes);

            // safely replace in map
            reentrantReadWriteLock.writeLock().lock();
            try {
                events.replace(id, event, eventNew);
            } finally {
                reentrantReadWriteLock.writeLock().unlock();
            }
        }
    }

    /**
     * remove marketRefType from event
     * get event for given id
     * if event found and contains given marketRefType
     *  then update event and replace in events map
     *
     * @param id - Identifier for event to remove the market from.
     * @param marketRefType - marketRefType to remove from event.
     */
    public void removeMarketRefTypeFromEvent(Integer id, MarketRefType marketRefType) {
        Event event = events.get(id);
        if (null != event && event.getMarketRefTypes().contains(marketRefType)) {
            // create new list of marketRefTypes
            // filter existing values ignoring marketRefType to be removed
            // append filtered values
            ArrayList<MarketRefType> marketRefTypes = new ArrayList<MarketRefType>(); // event.getMarketRefTypes().stream(
            event.getMarketRefTypes().stream().filter(e -> e != marketRefType).forEach(e -> marketRefTypes.add(e));
            Event eventNew = createEventWithNewMarketRefTypes(event, marketRefTypes);

            // safely replace in map
            reentrantReadWriteLock.writeLock().lock();
            try {
                events.replace(id, event, eventNew);
            } finally {
                reentrantReadWriteLock.writeLock().unlock();
            }
        }
    }

    /**
     * Get list of event names given a combination of category/subcategory/marketRefName
     * Filter depending on non-empty parameters
     *
     * @param catIn - Category ref identify the category to filter by.
     * @param subcatIn - SubCategory ref identify the sub-category to filter by.
     * @param marketRefNameIn - MarketRefType name to filter by.
     *
     * @return list of event names
     */
    public List<String> futureEventNamesCollection(String catIn, String subcatIn, String marketRefNameIn) {

        // Handle null values
        String cat = (null != catIn ? catIn : "");
        String subcat = (null != subcatIn ? subcatIn : "");
        String marketRefName = (null != marketRefNameIn ? marketRefNameIn : "");

        if (cat.length() > 0 && subcat.length() > 0 && marketRefName.length() > 0) {
            // Filter by cat, subcat and marketrefname
            logger.info("Filter by Category:{}; Subcategory:{}; MarketRefType:{}",
                    cat, subcat, marketRefName);
            return events.values().stream().filter(
                    e -> e.getSubCategory().getRef().equals(subcat)
                            && e.getSubCategory().getCategory().getRef().equals(cat)
                            && e.getMarketRefTypes().stream().map(MarketRefType::getMarketRefName).collect(Collectors.toList()).contains(marketRefName)
            ).map(Event::getName).collect(Collectors.toList());
        } else if (cat.length() > 0 && subcat.length() > 0) {
            // Filter by cat & subcat
            logger.info("Filter by Category:{}; Subcategory:{}", cat, subcat);
            return events.values().stream().filter(
                    e -> e.getSubCategory().getRef().equals(subcat)
                            && e.getSubCategory().getCategory().getRef().equals(cat)
            ).map(Event::getName).collect(Collectors.toList());
        } else if (cat.length() > 0 && marketRefName.length() > 0) {
            // Filter by cat and marketrefname
            logger.info("Filter by Category:{}; MarketRefType:{}", cat, marketRefName);
            return events.values().stream().filter(
                    e -> e.getSubCategory().getCategory().getRef().equals(cat)
                            && e.getMarketRefTypes().stream().map(MarketRefType::getMarketRefName).collect(Collectors.toList()).contains(marketRefName)
            ).map(Event::getName).collect(Collectors.toList());
        } else if (subcat.length() > 0 && marketRefName.length() > 0) {
            // Filter by subcat and marketrefname
            logger.info("Filter by Subcategory:{}; MarketRefType:{}", subcat, marketRefName);
            return events.values().stream().filter(
                    e -> e.getSubCategory().getRef().equals(subcat)
                            && e.getMarketRefTypes().stream().map(MarketRefType::getMarketRefName).collect(Collectors.toList()).contains(marketRefName)
            ).map(Event::getName).collect(Collectors.toList());
        } else if (subcat.length() > 0) {
            // Filter by subcat only
            logger.info("Filter only by Subcategory:{}", subcat);
            return events.values().stream().filter(
                    e -> e.getSubCategory().getRef().equals(subcat)
            ).map(Event::getName).collect(Collectors.toList());
        } else if (cat.length() > 0) {
            // Filter by cat only
            logger.info("Filter only by Category:{}", cat);
            return events.values().stream().filter(
                    e ->  e.getSubCategory().getCategory().getRef().equals(cat)
            ).map(Event::getName).collect(Collectors.toList());
        } else if (marketRefName.length() > 0) {
            // Filter by marketrefname only
            logger.info("Filter only by MarketRefType:{}", marketRefName);
            return events.values().stream().filter(
                    e -> e.getMarketRefTypes().stream().map(MarketRefType::getMarketRefName).collect(Collectors.toList()).contains(marketRefName)
            ).map(Event::getName).collect(Collectors.toList());
        }
        logger.info("Should NEVER reach here!");
        return new ArrayList<String>();
    }

    /**
     * dump full structure of the events map values
     * @return String
     */
    public String dumpFullStructure() {
        StringBuilder sb = new StringBuilder();
        for (Event e : events.values()) {
            sb.append(e.toString());
        }
        return sb.toString();
    }
}