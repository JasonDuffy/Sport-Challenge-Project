package de.hsesslingen.scpprojekt.scp.Database.Filler;

import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.*;
import de.hsesslingen.scpprojekt.scp.Database.Services.ActivityService;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Fills the Database with some test data
 *
 * @author Robin Hackh, Mason Schönherr
 */

@Component
public class Filler {
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private SportRepository sportRepository;
    @Autowired
    private ChallengeSportRepository challengeSportRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TeamMemberRepository teamMemberRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private BonusRepository bonusRepository;

    @Autowired
    @Lazy
    private ActivityService activityService;

    final private byte[] type1 = {2,3,4};
    final private byte[] type2 = {2,3,4};
    final private byte[] type3 = {2,3,4};
    final private byte[] type4 = {2,3,4};
    final private byte[] type5 = {2,3,4};

    final private LocalDateTime date1Start = LocalDateTime.of(2023,5,4,12,0); //cp1
    final private LocalDateTime date1End = LocalDateTime.of(2023,5,7,12,0);
    final private LocalDateTime date2Start = LocalDateTime.of(2023,2,15,12,0); //cp2
    final private LocalDateTime date2End = LocalDateTime.of(2023,6,15,12,0);
    final private LocalDateTime date3Start = LocalDateTime.of(2022,6,10,12,0); //cp3
    final private LocalDateTime date3End = LocalDateTime.of(2022,12,10,12,0);
    final private LocalDateTime date4Start = LocalDateTime.of(2023,1,1,12,0); //cp4
    final private LocalDateTime date4End = LocalDateTime.of(2023,12,31,12,0);
    final private LocalDateTime date5Start = LocalDateTime.of(2022,8,1,12,0); //cp5
    final private LocalDateTime date5End = LocalDateTime.of(2022,8,31,12,0);
    final private LocalDateTime date6Start = LocalDateTime.of(2021,5,1,12,0); //cp6
    final private LocalDateTime date6End = LocalDateTime.of(2023,12,24,12,0);
    final private LocalDateTime date7Start = LocalDateTime.of(2023,10,12,12,0); //cp7
    final private LocalDateTime date7End = LocalDateTime.of(2023,12,31,12,0);
    final private LocalDateTime date8Start = LocalDateTime.of(2022,12,20,12,0); //cp8
    final private LocalDateTime date8End = LocalDateTime.of(2023,3,20,12,0);
    final private LocalDateTime date1 = LocalDateTime.of(2023,5,5,12,0);
    final private LocalDateTime date2 = LocalDateTime.of(2023,5,5,12,0);
    final private LocalDateTime date3 = LocalDateTime.of(2023,5,6,12,0);
    final private LocalDateTime date4 = LocalDateTime.of(2023,2,2,12,0);
    final private LocalDateTime date5 = LocalDateTime.of(2023,4,18,12,0);
    final private LocalDateTime date6 = LocalDateTime.of(2022,7,12,12,0);
    final private LocalDateTime date7 = LocalDateTime.of(2023,5,23,12,0);
    final private LocalDateTime date8 = LocalDateTime.of(2023,5,28,12,0);
    final private LocalDateTime date9 = LocalDateTime.of(2023,6,1,12,0);
    final private LocalDateTime date10 = LocalDateTime.of(2023,3,12,12,0);
    final private LocalDateTime date11 = LocalDateTime.of(2023,3,30,12,0);
    final private LocalDateTime date12 = LocalDateTime.of(2022,8,9,12,0);
    final private LocalDateTime date13 = LocalDateTime.of(2022,8,7,12,0);
    final private LocalDateTime date14 = LocalDateTime.of(2022,8,24,12,0);
    final private LocalDateTime date15 = LocalDateTime.of(2022,7,29,12,0);
    final private LocalDateTime date16 = LocalDateTime.of(2022,7,30,12,0);
    final private LocalDateTime date17 = LocalDateTime.of(2023,10,31,12,0);
    final private LocalDateTime date18 = LocalDateTime.of(2022,12,22,12,0);
    final private LocalDateTime date19 = LocalDateTime.of(2023,5,7,8,0);
    final private LocalDateTime date20 = LocalDateTime.of(2023,5,5,20,0);
    final private LocalDateTime date21 = LocalDateTime.of(2023,5,6,12,0);
    final private LocalDateTime date22 = LocalDateTime.of(2023,3,9,12,0);
    final private LocalDateTime date23 = LocalDateTime.of(2023,4,1,12,0);
    final private LocalDateTime date24 = LocalDateTime.of(2022,8,2,12,0);
    final private LocalDateTime date25 = LocalDateTime.of(2023,3,3,12,0);
    final private LocalDateTime date26 = LocalDateTime.of(2023,5,1,12,0);
    final private LocalDateTime date27 = LocalDateTime.of(2023,6,2,12,0);
    final private LocalDateTime date28 = LocalDateTime.of(2023,2,19,12,0);
    final private LocalDateTime date29 = LocalDateTime.of(2023,2,17,12,0);
    final private LocalDateTime date30 = LocalDateTime.of(2022,8,6,12,0);
    final private LocalDateTime date31 = LocalDateTime.of(2022,8,12,12,0);
    final private LocalDateTime date32 = LocalDateTime.of(2022,8,18,12,0);
    final private LocalDateTime date33 = LocalDateTime.of(2023,3,20,12,0);
    final private LocalDateTime date34 = LocalDateTime.of(2022,9,20,12,0);
    final private LocalDateTime date35 = LocalDateTime.of(2023,11,30,12,0);
    final private LocalDateTime date36 = LocalDateTime.of(2022,12,28,12,0);
    final private LocalDateTime date37 = LocalDateTime.of(2023,5,6,20,0);
    final private LocalDateTime date38 = LocalDateTime.of(2023,5,5,17,30);
    final private LocalDateTime date39 = LocalDateTime.of(2023,5,7,10,0);
    final private LocalDateTime date40 = LocalDateTime.of(2023,3,4,12,0);
    final private LocalDateTime date41 = LocalDateTime.of(2023,3,8,12,0);
    final private LocalDateTime date42 = LocalDateTime.of(2022,9,6,12,0);
    final private LocalDateTime date43 = LocalDateTime.of(2023,5,14,12,0);
    final private LocalDateTime date44 = LocalDateTime.of(2023,5,12,12,0);
    final private LocalDateTime date45 = LocalDateTime.of(2023,6,3,12,0);
    final private LocalDateTime date46 = LocalDateTime.of(2023,2,22,12,0);
    final private LocalDateTime date47 = LocalDateTime.of(2023,3,20,12,0);
    final private LocalDateTime date48 = LocalDateTime.of(2022,8,4,12,0);
    final private LocalDateTime date49 = LocalDateTime.of(2022,8,19,12,0);
    final private LocalDateTime date50 = LocalDateTime.of(2022,8,12,12,0);
    final private LocalDateTime date51 = LocalDateTime.of(2023,1,21,12,0);
    final private LocalDateTime date52 = LocalDateTime.of(2023,1,2,12,0);
    final private LocalDateTime date53 = LocalDateTime.of(2023,11,28,12,0);
    final private LocalDateTime date54 = LocalDateTime.of(2022,12,25,12,0);

    Member joe = new Member("Joe.Doe@example.com", "Joe", "Doe", null, true);
    Member hanna = new Member("Hanna.Montana@example.com", "Hannah", "Montana", null, false);
    Member god = new Member("God.Rick@example.com", "God", "Rick", null, false);
    Member jack = new Member("Jack.Sparrow@example.com", "Jack", "Sparrow", null, false);
    Member anakin = new Member("Anakin.Skywalker@example.com", "Anakin", "Skywalker", null, false);
    Member kevin = new Member("kevin.kevin@bsp.de", "Kevin", "Kevin", null, false);
    Member laura = new Member("laura.kimpel@bsp.de", "Laura", "Kimpel", null, true);
    Member darth = new Member("darth.vader@bsp.de", "Darth", "Vader", null, true);
    Member spongebob = new Member("spongebob.schwammkopf@bsp.de", "Spongebob", "Schwammkopf", null, false);
    Member ryu = new Member("ryu.hayabusa@bsp.de", "Ryu", "Hayabusa", null, false);
    Member dante = new Member("dante.sparda@bsp.de", "Dante", "Sparda", null, false);
    Member crash = new Member("crash.bandicoot@bsp.de", "Crash", "Bandicoot", null, false);
    Member david = new Member("david.sprung@bsp.de", "David", "Sprung", null, false);
    Member vergil = new Member("vergil.sparda@bsp.de", "Vergil", "Sparda", null, false);
    Member master = new Member("master.chief@bsp.de", "Master", "Chief", null, false);
    Member cloud = new Member("cloud.strife@bsp.de", "Cloud", "Strife", null, false);
    Member tifa = new Member("tifa.lockhart@bsp.de", "Tifa", "Lockhart", null, false);
    Member aerith = new Member("aerith.gainsborough@bsp.de", "Aerith", "Gainsborough", null, false);
    Member squall = new Member("squall.leonheart@bsp.de", "Squall", "Leonheart", null, false);
    Member donald = new Member("donald.trump@bsp.de", "Donald", "Trump", null, false);
    Member barrack = new Member("barrack.obama@bsp.de", "Barrack", "Obama", null, false);
    Member zack = new Member("zack.fair@bsp.de", "Zack", "Fair", null, false);
    Member oliver = new Member("oliver.sikes@bsp.de", "Oliver", "Sikes", null, false);
    Member ryan = new Member("ryan.reynolds@bsp.de", "Ryan", "Reynolds", null, false);
    Member dwayne = new Member("dwayne.johnson@bsp.de", "Dwayne", "Johnson", null, false);
    Member dan = new Member("dan.reynolds@bsp.de", "Dan", "Reynolds", null, false);
    Member obiwan = new Member("obiwan.kenobi@bsp.de", "Obiwan", "Kenobi", null, false);
    Member luke = new Member("luke.skywalker@bsp.de", "Luke", "Skywalker", null, false);
    Member boba = new Member("boba.fet@bsp.de", "Boba", "Fet", null, false);
    Member arnold = new Member("arnold.schwarznegger@bsp.de", "Arnold", "Schwarznegger", null, false);
    Member leonardo = new Member("leonardo.davinci@bsp.de", "Leonardo", "Davinci", null, false);
    Member elon = new Member("elon.musk@bsp.de", "Elon", "Musk", null, false);
    Member mickey = new Member("mickey.mouse@bsp.de", "Mickey", "Mouse", null, false);
    Member minnie = new Member("minnie.mouse@bsp.de", "Minnie", "Mouse", null, false);
    Member lara = new Member("lara.croft@bsp.de", "Lara", "Croft", null, false);
    Member davis = new Member("davis.schulz@bsp.de", "Davis", "Schulz", null, false);
    Member morgan = new Member("morgan.freeman@bsp.de", "Morgan", "Freeman", null, false);
    Member jim = new Member("jim.carry@bsp.de", "Jim", "Carry", null, false);
    Member will = new Member("will.smith@bsp.de", "Will", "Smith", null, false);
    Member terry = new Member("terry.cruise@bsp.de", "Terry", "Cruise", null, false);
    Member noctis = new Member("noctis.lucis@bsp.de", "Noctis", "Lucis", null, false);
    Member jeeper = new Member("jeeper.creeper@bsp.de", "Jeeper", "Creeper", null, false);
    Member achim = new Member("achim.lutz@bsp.de", "Achim", "Lutz", null, false);
    Member felix = new Member("felix.kjellberg@bsp.de", "Felix", "Kjellberg", null, false);
    Member mark = new Member("mark.fischbach@bsp.de", "Mark", "Fischbach", null, false);
    Member optimus = new Member("optimus.prime@bsp.de", "Optimus", "Prime", null, false);
    Member bumble = new Member("bumble.bee@bsp.de", "Bumble", "Bee", null, false);
    Member helene = new Member("helene.fischer@bsp.de", "Helene", "Fischer", null, false);
    Member robin = new Member("robin.hackh@bsp.de", "Robin", "Hackh", null, false);

    Image pic1 = new Image("Hustle", "image/jpg", type1);
    Image pic2 = new Image("Bustle", "image/jpg", type2);
    Image pic3 = new Image("Rad", "image/jpg", type3);
    Image pic4 = new Image("Kanu", "image/jpg", type4);
    Image pic5 = new Image("Skateboard", "image/jpg", type5);

    Challenge hustle = new Challenge("Hustle", "Welches Team kann mehr Km an 3 Tagen zurücklegen?", date1Start, date1End, pic1, 2000);
    Challenge bustle = new Challenge("Bustle", "Welches Team erreicht das Ziel näher während dem Ausflug?", date2Start, date2End, pic2, 1000);
    Challenge anniversary = new Challenge("Anniversary", "Zum Jahrestag gibt es eine weitere Challenge. Sieger erhalten ein Essen auf Kosten der Verlierer!", date3Start, date3End, pic3, 5000);
    Challenge one = new Challenge("One-Year-Challenge", "Welches Team schafft die meisten Km über das Jahr?", date4Start, date4End, pic4, 15000);
    Challenge summer = new Challenge("Summer fun!", "Auch im Sommer ist Bewegung wichtig! Holt euch die Kilometer in der Sonne!", date5Start, date5End, pic5, 2500);
    Challenge ps5 = new Challenge("PS5 Challenge!", "Das Team, das zu erst das Ziel erreicht gewinnt eine PS5!", date6Start, date6End, pic2, 1000);
    Challenge spaceday = new Challenge("Spaceday!", "Welches Team erreicht das Ziel zu erst im Weltraum??", date7Start, date7End, pic1, 500);
    Challenge winter = new Challenge("Rodelspaß!", "Rodeln ist angesagt! Schnappt euch einen Schlitten und ad dafür!", date8Start, date8End, pic3, 250);

    Sport laufen = new Sport("Laufen", 3);
    Sport radfahren = new Sport("Radfahren",1);
    Sport pogo = new Sport("Pogohüpfen", 5);
    Sport kanoo = new Sport("Kanoofahren", 6);
    Sport skaten = new Sport("Skaten", 2);
    Sport rodeln = new Sport("Rodeln", 4);

    ChallengeSport cp1a = new ChallengeSport(3, hustle, laufen);
    ChallengeSport cp1b = new ChallengeSport(1, hustle, radfahren);
    ChallengeSport cp1c = new ChallengeSport(2, hustle, skaten);
    ChallengeSport cp2a = new ChallengeSport(1, bustle, radfahren);
    ChallengeSport cp2b = new ChallengeSport(2, bustle, skaten);
    ChallengeSport cp3 = new ChallengeSport(4, anniversary, pogo);
    ChallengeSport cp4a = new ChallengeSport(1, one, skaten);
    ChallengeSport cp4b = new ChallengeSport(3, one, laufen);
    ChallengeSport cp4c = new ChallengeSport(2, one, radfahren);
    ChallengeSport cp4d = new ChallengeSport(6, one, kanoo);
    ChallengeSport cp4e = new ChallengeSport(5, one, pogo);
    ChallengeSport cp5a = new ChallengeSport(6, summer, kanoo);
    ChallengeSport cp5b = new ChallengeSport(3, summer, laufen);
    ChallengeSport cp5c = new ChallengeSport(1, summer, radfahren);
    ChallengeSport cp6a = new ChallengeSport(2, ps5, skaten);
    ChallengeSport cp6b = new ChallengeSport(1, ps5, radfahren);
    ChallengeSport cp7 = new ChallengeSport(5, spaceday, pogo);
    ChallengeSport cp8 = new ChallengeSport(4, winter, rodeln);


    Team red = new Team("Team Red", pic1, hustle);
    Team redHater = new Team("Team RedHater", pic1, hustle);
    Team blue = new Team("Team Blue", pic2, hustle);
    Team blue2 = new Team("Team Blue2", pic2, bustle);
    Team blueHater = new Team("Team Blue Hater", pic2, bustle);
    Team besten = new Team("Die Besten", pic3, anniversary);
    Team polka = new Team("Team Polka", pic3, anniversary);
    Team social = new Team("Team Social", pic3, anniversary);
    Team antiSocial = new Team("Team Anti Social", pic3, anniversary);
    Team anderen = new Team("Die Anderen", pic4, one);
    Team blaze = new Team("Team Blaze", pic4, one);
    Team chase = new Team("Team Chase", pic4, one);
    Team musketiere = new Team("Die drei Musketiere und ihre Handlanger", pic5, summer);
    Team dynamite = new Team("Team Dynamite", pic5, summer);
    Team villains = new Team("Die Villains", pic5, ps5);
    Team gamer = new Team("Team Gamer", pic5, ps5);
    Team powerRangers = new Team("Die Power Rangers", pic5, spaceday);
    Team autobots = new Team("Team Autobots", pic5, spaceday);
    Team metalheads = new Team("Die Metal Heads", pic5, winter);
    Team schlagerfans = new Team("Die Schlagerfans", pic5, winter);

    TeamMember tm1 = new TeamMember(red, joe);
    TeamMember tm2 = new TeamMember(red, hanna);
    TeamMember tm3 = new TeamMember(red, god);
    TeamMember tm4 = new TeamMember(red, jack);
    TeamMember tm5 = new TeamMember(redHater, anakin);
    TeamMember tm6 = new TeamMember(redHater, kevin);
    TeamMember tm7 = new TeamMember(blue, laura);
    TeamMember tm8 = new TeamMember(blue, darth);
    TeamMember tm9 = new TeamMember(blue2, spongebob);
    TeamMember tm10 = new TeamMember(blue2, ryu);
    TeamMember tm11 = new TeamMember(blue2, dante);
    TeamMember tm12 = new TeamMember(blueHater, crash);
    TeamMember tm13 = new TeamMember(blueHater, david);
    TeamMember tm14 = new TeamMember(besten, vergil);
    TeamMember tm15 = new TeamMember(besten, master);
    TeamMember tm16 = new TeamMember(besten, cloud);
    TeamMember tm17 = new TeamMember(polka, tifa);
    TeamMember tm18 = new TeamMember(polka, aerith);
    TeamMember tm19 = new TeamMember(social, squall);
    TeamMember tm20 = new TeamMember(social, donald);
    TeamMember tm21 = new TeamMember(antiSocial, barrack);
    TeamMember tm22 = new TeamMember(antiSocial, zack);
    TeamMember tm23 = new TeamMember(anderen, oliver);
    TeamMember tm24 = new TeamMember(anderen, ryan);
    TeamMember tm25 = new TeamMember(anderen, dwayne);
    TeamMember tm26 = new TeamMember(blaze, dan);
    TeamMember tm27 = new TeamMember(blaze, obiwan);
    TeamMember tm28 = new TeamMember(chase, luke);
    TeamMember tm29 = new TeamMember(chase, boba);
    TeamMember tm30 = new TeamMember(chase, arnold);
    TeamMember tm31 = new TeamMember(musketiere, leonardo);
    TeamMember tm32 = new TeamMember(musketiere, elon);
    TeamMember tm33 = new TeamMember(dynamite, jeeper);
    TeamMember tm34 = new TeamMember(dynamite, achim);
    TeamMember tm35 = new TeamMember(villains, mickey);
    TeamMember tm36 = new TeamMember(villains, minnie);
    TeamMember tm37 = new TeamMember(villains, lara);
    TeamMember tm38 = new TeamMember(gamer, felix);
    TeamMember tm39 = new TeamMember(gamer, mark);
    TeamMember tm40 = new TeamMember(powerRangers, davis);
    TeamMember tm41 = new TeamMember(powerRangers, morgan);
    TeamMember tm42 = new TeamMember(powerRangers, jim);
    TeamMember tm43 = new TeamMember(autobots, optimus);
    TeamMember tm44 = new TeamMember(autobots, bumble);
    TeamMember tm45 = new TeamMember(metalheads, will);
    TeamMember tm46 = new TeamMember(metalheads, terry);
    TeamMember tm47 = new TeamMember(metalheads, noctis);
    TeamMember tm48 = new TeamMember(schlagerfans, helene);
    TeamMember tm49 = new TeamMember(schlagerfans, robin);

    Activity act1 = new Activity(cp1a, joe, 4, date1);
    Activity act2 = new Activity(cp1b, hanna, 3, date2);
    Activity act3 = new Activity(cp1c, god, 5, date3);
    Activity act4 = new Activity(cp2a, spongebob, 7, date4);
    Activity act5 = new Activity(cp2b, ryu, 2, date5);
    Activity act6 = new Activity(cp3, vergil, 8, date6);
    Activity act7 = new Activity(cp4a, oliver, 1, date7);
    Activity act8 = new Activity(cp4b, ryan, 2, date8);
    Activity act9 = new Activity(cp4c, dwayne, 3, date9);
    Activity act10 = new Activity(cp4d, dan, 4, date10);
    Activity act11 = new Activity(cp4e, obiwan, 5, date11);
    Activity act12 = new Activity(cp5a, leonardo, 6, date12);
    Activity act13 = new Activity(cp5b, elon, 1, date13);
    Activity act14 = new Activity(cp5c, jeeper, 2, date14);
    Activity act15 = new Activity(cp6a, mickey, 6, date15);
    Activity act16 = new Activity(cp6b, minnie, 5, date16);
    Activity act17 = new Activity(cp7, davis, 2, date17);
    Activity act18 = new Activity(cp8, will, 3, date18);
    Activity act19 = new Activity(cp1a, jack, 4, date19);
    Activity act20 = new Activity(cp1b, anakin, 9, date20);
    Activity act21 = new Activity(cp1c, laura, 7, date21);
    Activity act22 = new Activity(cp2a, dante, 3, date22);
    Activity act23 = new Activity(cp2b, crash, 2, date23);
    Activity act24 = new Activity(cp3, master, 6, date24);
    Activity act25 = new Activity(cp4a, luke, 3, date25);
    Activity act26 = new Activity(cp4b, boba, 8, date26);
    Activity act27 = new Activity(cp4c, arnold, 1, date27);
    Activity act28 = new Activity(cp4d, oliver, 1, date28);
    Activity act29 = new Activity(cp4e, ryan, 2, date29);
    Activity act30 = new Activity(cp5a, achim, 3, date30);
    Activity act31 = new Activity(cp5b, leonardo, 3, date31);
    Activity act32 = new Activity(cp5c, elon, 4, date32);
    Activity act33 = new Activity(cp6a, lara, 5, date33);
    Activity act34 = new Activity(cp6b, felix, 6, date34);
    Activity act35 = new Activity(cp7, morgan, 2, date35);
    Activity act36 = new Activity(cp8, terry, 4, date36);
    Activity act37 = new Activity(cp1a, darth, 4, date37);
    Activity act38 = new Activity(cp1b, joe, 3, date38);
    Activity act39 = new Activity(cp1c, hanna, 5, date39);
    Activity act40 = new Activity(cp2a, david, 7, date40);
    Activity act41 = new Activity(cp2b, spongebob, 2, date41);
    Activity act42 = new Activity(cp3, tifa, 3, date42);
    Activity act43 = new Activity(cp4a, dwayne, 1, date43);
    Activity act44 = new Activity(cp4b, dan, 10, date44);
    Activity act45 = new Activity(cp4c, obiwan, 9, date45);
    Activity act46 = new Activity(cp4d, luke, 4, date46);
    Activity act47 = new Activity(cp4e, arnold, 2, date47);
    Activity act48 = new Activity(cp5a, jeeper, 5, date48);
    Activity act49 = new Activity(cp5b, achim, 6, date49);
    Activity act50 = new Activity(cp5c, leonardo, 7, date50);
    Activity act51 = new Activity(cp6a, mark, 8, date51);
    Activity act52 = new Activity(cp6b, mickey, 3, date52);
    Activity act53 = new Activity(cp7, optimus, 2, date53);
    Activity act54 = new Activity(cp8, robin, 1, date54);


    Bonus doubA = new Bonus(cp1a, date1Start, date1End, 2, "DoubleXP Weekend", "Doppelte Kilomete übers Wochenende");
    Bonus doubB = new Bonus(cp1b, date1Start, date1End, 2, "DoubleXP Weekend", "Doppelte Kilomete übers Wochenende");
    Bonus doubC = new Bonus(cp1c, date1Start, date1End, 2, "DoubleXP Weekend", "Doppelte Kilomete übers Wochenende");

    Bonus anniA = new Bonus(cp2a, date2Start, date2End, 3, "Anniversary", "Wegen Anniversary gibt es mehr Kilometer!");
    Bonus anniB = new Bonus(cp2b, date2Start, date2End, 3, "Anniversary", "Wegen Anniversary gibt es mehr Kilometer!");

    Bonus holi = new Bonus(cp3, date3Start, date3End, 4, "Holiday Event", "Während den Ferien gibt es mehr KM!");

    Bonus finishA = new Bonus(cp4a, date4Start, date4End, 2, "Finished Project", "Aufgrund des beendeten Projekts gibt es mehr Kilometeer für alle!");
    Bonus finishB = new Bonus(cp4b, date4Start, date4End, 2, "Finished Project", "Aufgrund des beendeten Projekts gibt es mehr Kilometeer für alle!");
    Bonus finishC = new Bonus(cp4c, date4Start, date4End, 2, "Finished Project", "Aufgrund des beendeten Projekts gibt es mehr Kilometeer für alle!");
    Bonus finishD = new Bonus(cp4d, date4Start, date4End, 2, "Finished Project", "Aufgrund des beendeten Projekts gibt es mehr Kilometeer für alle!");
    Bonus finishE = new Bonus(cp4e, date4Start, date4End, 2, "Finished Project", "Aufgrund des beendeten Projekts gibt es mehr Kilometeer für alle!");

    Bonus luckyA = new Bonus(cp5a, date5Start, date5End, 3, "Lucky Day!", "Für heute gibt es mehr Kilometer!");
    Bonus luckyB = new Bonus(cp5b, date5Start, date5End, 3, "Lucky Day!", "Für heute gibt es mehr Kilometer!");
    Bonus luckyC = new Bonus(cp5c, date5Start, date5End, 3, "Lucky Day!", "Für heute gibt es mehr Kilometer!");

    @EventListener(ApplicationReadyEvent.class)
    public void fillDb() throws NotFoundException {
        try {
            Image[] imgArray = {pic1, pic2, pic3, pic4, pic5};

            for (int i = 0; i < imgArray.length; i++) {
                InputStream ip = new FileInputStream("src/main/java/de/hsesslingen/scpprojekt/scp/Database/Filler/Images/image-" + (i + 1) + ".jpg");
                BufferedImage bi = ImageIO.read(ip);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bi, "jpg", baos);
                imgArray[i].setData(baos.toByteArray());
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        memberRepository.saveAll(Arrays.asList(
                joe, hanna, god, jack, anakin, kevin, laura, darth, spongebob, ryu, dante, crash,
                david, vergil, master, cloud, tifa, aerith, squall, donald, barrack, zack, oliver,
                ryan, dwayne, luke, boba, arnold, leonardo, elon, mickey, minnie, lara, davis,
                morgan, jim, will, terry, noctis, jeeper, achim, felix, mark, optimus, bumble,
                helene, robin
        ));

        imageRepository.saveAll(Arrays.asList(
                pic1, pic2, pic3, pic4, pic5
        ));

        sportRepository.saveAll(Arrays.asList(
                laufen, radfahren, pogo, kanoo, skaten, rodeln
        ));

        challengeRepository.saveAll(Arrays.asList(
                hustle, bustle, anniversary, one, summer, ps5, spaceday, winter
        ));

        challengeSportRepository.saveAll(Arrays.asList(
                cp1a, cp1b, cp1c, cp2a, cp2b, cp3, cp4a, cp4b, cp4c, cp4d, cp4e, cp5a, cp5b,
                cp5c, cp6a, cp6b, cp7, cp8
        ));

        teamRepository.saveAll(Arrays.asList(
                red, redHater, blue, blue2, blueHater, besten, polka, social, antiSocial,
                anderen, blaze, chase, musketiere, dynamite, villains, gamer, powerRangers, autobots,
                metalheads, schlagerfans
        ));

        teamMemberRepository.saveAll(Arrays.asList(
                tm1, tm2, tm3, tm4, tm5, tm6, tm7, tm8, tm9, tm10, tm11, tm12, tm13, tm14, tm15,
                tm16, tm17, tm18, tm19, tm20, tm21, tm22, tm23, tm24, tm25, tm26, tm27, tm28, tm29,
                tm30, tm31, tm32, tm33, tm34, tm35, tm36, tm37, tm38, tm39, tm40, tm41, tm42, tm43,
                tm44, tm45, tm46, tm47, tm48, tm49
        ));

        activityRepository.saveAll(Arrays.asList(
                act1, act2, act3, act4, act5, act6, act7, act8, act9, act10, act11, act12, act13,
                act14, act15, act16, act17, act18, act19, act20, act21, act22, act23, act24, act25,
                act26, act27, act28, act29, act30, act31, act32, act33, act34, act35, act36, act37,
                act38, act39, act40, act41, act42, act43, act44, act45, act46, act47, act48, act49,
                act50, act51, act52, act53, act54
        ));

        bonusRepository.saveAll(Arrays.asList(
                doubA, doubB, doubC, anniA, anniB, holi, finishA, finishB, finishC, finishD, finishE,
                luckyA, luckyB, luckyC
        ));

        activityService.totalDistanceAll();
    }
}

