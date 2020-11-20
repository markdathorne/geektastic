package com.fsbtech.interviews;

import com.fsbtech.interviews.entities.Category;
import com.fsbtech.interviews.entities.Event;
import com.fsbtech.interviews.entities.MarketRefType;
import com.fsbtech.interviews.entities.SubCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BaseClientTest {

    final static Logger logger = LogManager.getLogger(BaseClientTest.class);

    BaseClient client = new BaseClient();
    Category categoryFootball = new Category(1, "Football");
    Category categoryCricket = new Category(2, "Cricket");
    Category categoryTennis = new Category(3, "Tennis");

    SubCategory subCatFootballPremierLeague = new SubCategory(1, "Premier League", categoryFootball);
    SubCategory subCatFootballChampionshipLeague = new SubCategory(2, "Championship League", categoryFootball);
    SubCategory subCatFootballDivision1League = new SubCategory(3, "Division 1 League", categoryFootball);
    SubCategory subCatCricketIPL = new SubCategory(4, "Indian Premier League", categoryCricket);
    SubCategory subCatCricketCPL = new SubCategory(5, "Caribbean Premier League", categoryCricket);
    SubCategory subCatTennisWimbledon = new SubCategory(6, "Wimbledon", categoryTennis);
    SubCategory subCatTennisUSOpen = new SubCategory(7, "US Open", categoryTennis);
    SubCategory subCatTennisAUSOpen = new SubCategory(8, "Australia Open", categoryTennis);
    SubCategory subCatTennisFrenchOpen = new SubCategory(9, "French Open", categoryTennis);

    MarketRefType marketRefTypeHome = new MarketRefType(1, "Home");
    MarketRefType marketRefTypeAway = new MarketRefType(2, "Away");
    MarketRefType marketRefTypeDraw = new MarketRefType(3, "Draw");
    MarketRefType[] marketRefTypesFootie = {marketRefTypeHome, marketRefTypeAway, marketRefTypeDraw};
    MarketRefType[] marketRefTypesCricket = {marketRefTypeHome, marketRefTypeAway, marketRefTypeDraw};
    MarketRefType[] marketRefTypesTennis = {marketRefTypeHome, marketRefTypeAway};

    void addPremierLeagueEvent(String eventName) {
        Event event = new Event(client.getNextEventId(), eventName, subCatFootballPremierLeague, Arrays.asList(marketRefTypesFootie), false );
        client.addEvent(event);
    }
    void addChampionshipLeagueEvent(String eventName) {
        Event event = new Event(client.getNextEventId(), eventName, subCatFootballChampionshipLeague, Arrays.asList(marketRefTypesFootie), false );
        client.addEvent(event);
    }
    void addDivision1LeagueEvent(String eventName) {
        Event event = new Event(client.getNextEventId(), eventName, subCatFootballDivision1League, Arrays.asList(marketRefTypesFootie), false );
        client.addEvent(event);
    }
    void addWimbledonEvent(String eventName) {
        Event event = new Event(client.getNextEventId(), eventName, subCatTennisWimbledon, Arrays.asList(marketRefTypesTennis), false );
        client.addEvent(event);
    }
    void addUSOpenEvent(String eventName) {
        Event event = new Event(client.getNextEventId(), eventName, subCatTennisUSOpen, Arrays.asList(marketRefTypesTennis), false );
        client.addEvent(event);
    }
    void addAUSOpenEvent(String eventName) {
        Event event = new Event(client.getNextEventId(), eventName, subCatTennisAUSOpen, Arrays.asList(marketRefTypesTennis), false );
        client.addEvent(event);
    }
    void addFrenchOpenEvent(String eventName) {
        Event event = new Event(client.getNextEventId(), eventName, subCatTennisFrenchOpen, Arrays.asList(marketRefTypesTennis), false );
        client.addEvent(event);
    }
    void addIPLEvent(String eventName) {
        Event event = new Event(client.getNextEventId(), eventName, subCatCricketIPL, Arrays.asList(marketRefTypesCricket), false );
        client.addEvent(event);
    }
    void addCPLEvent(String eventName) {
        Event event = new Event(client.getNextEventId(), eventName, subCatCricketCPL, Arrays.asList(marketRefTypesCricket), false );
        client.addEvent(event);
    }

    void setupCricket() {
        addIPLEvent("Chennai Superkings vs Delhi Capitals");
        addIPLEvent("Kings XI Punjab vs Kolkata Knightriders");
        addCPLEvent("Jamaica Tallawahs vs St Lucia Zouks");
        addCPLEvent("Guyana Amazon Warriors vs Barbados Tridents");
        addCPLEvent("St Kitts & Nevis Patriots vs Trinbago Knight Riders");
    }
    void setupTennis() {
        addWimbledonEvent("M Navratilova vs C Evert");
        addWimbledonEvent("BJ King vs E Goolagong");
        addWimbledonEvent("B Becker vs J McEnroe");
        addWimbledonEvent("V Williams vs S Williams");
        addWimbledonEvent("P Sampras vs R Federer");
        addUSOpenEvent("J McEnroe vs P Sampras");
        addAUSOpenEvent("A Agassi vs A Ashe");
        addFrenchOpenEvent("J Connors vs J McEnroe");
    }

    void setupFootball() {
        addPremierLeagueEvent("West Ham vs Fulham");
        addPremierLeagueEvent("Everton vs Fulham");
        addPremierLeagueEvent("Liverpool vs Fulham");
        addPremierLeagueEvent("Arsenal vs Fulham");
        addPremierLeagueEvent("Crystal Palace vs Leeds Utd");
        addPremierLeagueEvent("Brighton vs Sheff Utd");
        addChampionshipLeagueEvent("Norwich vs West Brom");
        addChampionshipLeagueEvent("Charlton vs Millwall");
        addChampionshipLeagueEvent("Norwich vs West Brom");
        addDivision1LeagueEvent("Doncaster Rovers vs Fleet");
    }

    @BeforeEach
    void setupClient() {
        client = new BaseClient();
        client.resetEventId();
        setupFootball();
        setupCricket();
        setupTennis();
    }

    @Test
    void addEvent() {
        assertTrue(client.events.size() > 0);
        assertTrue(client.events.values().size() > 4);
    }

    @Test
    void eventCompleted() {
        assertTrue(null != client);
        assertTrue(null != client.events);
        assertTrue(client.events.size() > 0);
        assertTrue(client.events.containsKey(1));

        Event eventWestHamVsFulham = client.getEvent(1);
        client.eventCompleted(1);
        assertFalse(client.events.containsKey(1));
        assertTrue(client.eventsArchive.containsKey(1));
        client.eventCompleted(2);
        client.eventCompleted(3);
        client.eventCompleted(4);
        assertFalse(client.events.containsKey(2));
        assertTrue(client.eventsArchive.containsKey(2));
        assertFalse(client.events.containsKey(3));
        assertTrue(client.eventsArchive.containsKey(3));
        assertFalse(client.events.containsKey(4));
        assertTrue(client.eventsArchive.containsKey(4));
        logger.info("Events Archive size (should be 19) is {}",client.eventsArchive.size());
        logger.info("Events size (should be 4) is {}",client.events.size());
        assertTrue(client.eventsArchive.size() == 4);
        assertTrue(client.events.size() == 19);
        logger.info(client.dumpFullStructure());
    }

    @Test
    void attachMarketRefTypeToEvent() {
        logger.info("Attach additional MarketRefType 'Unknown' from event 6");
        MarketRefType marketRefTypeUnknown = new MarketRefType(4, "Unknown");
        client.attachMarketRefTypeToEvent(6, marketRefTypeUnknown);
        assertTrue(client.events.get(6).getMarketRefTypes().contains(marketRefTypeUnknown));
        assertFalse(client.events.get(5).getMarketRefTypes().contains(marketRefTypeUnknown));
    }

    @Test
    void removeMarketRefTypeFromEvent() {
        Integer eventId = 8;
        logger.debug("Remove existing MarketRefType 'Home' from event {}", eventId);
        Event event = client.events.get(eventId);
        logger.info("Event: {}", event);
        assertTrue(event.getMarketRefTypes().contains(marketRefTypeHome));
        client.removeMarketRefTypeFromEvent(eventId, marketRefTypeHome);
        assertFalse(client.events.get(eventId).getMarketRefTypes().contains(marketRefTypeHome));
        assertTrue(client.events.get(eventId).getMarketRefTypes().contains(marketRefTypeAway));
        event = client.events.get(eventId);
        logger.info("Event {}", event);
    }

    @Test
    void futureEventNamesCollectionParam3Unknown() {
        logger.info("futureEventsCollection 3 parameter test");
        // create special case MarketRefType
        MarketRefType marketRefTypeUnknown = new MarketRefType(4, "Unknown");
        Integer eventId = 6;
        Event event = client.getEvent(eventId);
        logger.info("Should select event id {}, {}:{}", eventId, event.getId(), event.getName());
        client.attachMarketRefTypeToEvent(6, marketRefTypeUnknown);

        // select events
        List<String> eventsSelected = client.futureEventNamesCollection("Football", "Premier League", "Unknown");
        logger.info("eventsSelected size is {}", eventsSelected.size());
        eventsSelected.stream().forEach(e ->  logger.info(e));
        assertTrue(eventsSelected.contains(event.getName()));
        assertTrue(eventsSelected.size() == 1);
    }

    @Test
    void futureEventNamesCollectionParam2PremierLeague() {
        logger.info("futureEventsCollection 2 parameter test");
        logger.info("Should select events for {}, {}", "Football", "Premier League");
        List<String> eventsSelected = client.futureEventNamesCollection("Football", "Premier League", "");
        logger.info("eventsSelected size (should be 6) is {}", eventsSelected.size());
        eventsSelected.stream().forEach(e ->  logger.info(e));
        assertTrue(eventsSelected.size() == 6);
    }

    @Test
    void futureEventNamesCollectionParam1Football() {
        logger.info("futureEventsCollection 2 parameter test");
        logger.info("Should select events for {}", "Football");
        List<String> eventsSelected = client.futureEventNamesCollection("Football", "", "");
        logger.info("eventsSelected size (should be 10) is {}", eventsSelected.size());
        eventsSelected.stream().forEach(e ->  logger.info(e));
        assertTrue(eventsSelected.size() == 10);
    }

    @Test
    void futureEventNamesCollectionParam1Tennis() {
        logger.info("futureEventsCollection 2 parameter test");
        logger.info("Should select events for {}", "Tennis");
        List<String> eventsSelected = client.futureEventNamesCollection("Tennis", "", "");
        logger.info("eventsSelected size (should be 8) is {}", eventsSelected.size());
        eventsSelected.stream().forEach(e ->  logger.info(e));
        assertTrue(eventsSelected.size() == 8);
    }

    @Test
    void futureEventNamesCollectionParam1Draw() {
        logger.info("futureEventsCollection 1 parameter test");
        logger.info("Should select events for {}", "Draw");
        List<String> eventsSelected = client.futureEventNamesCollection("", "", "Draw");
        logger.info("eventsSelected size (should be 15) is {}", eventsSelected.size());
        eventsSelected.stream().forEach(e ->  logger.info(e));
        assertTrue(eventsSelected.size() == 15);
    }

}