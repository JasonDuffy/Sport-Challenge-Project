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
    private MemberRepository memberRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private BonusRepository bonusRepository;
    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private ChallengeSportRepository challengeSportRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private SportRepository sportRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TeamMemberRepository teamMemberRepository;
    @Autowired
    private ChallengeSportBonusRepository challengeSportBonusRepository;
    @Autowired
    @Lazy
    private ActivityService activityService;

    final private byte[] type1 = {2,3,4};
    final private byte[] type2 = {2,3,4};
    final private byte[] type3 = {2,3,4};
    final private byte[] type4 = {2,3,4};
    final private byte[] type5 = {2,3,4};

    final private LocalDateTime date1Start = LocalDateTime.of(2023,5,4,12,0);
    final private LocalDateTime date1End = LocalDateTime.of(2023,5,7,12,0);
    final private LocalDateTime date2Start = LocalDateTime.of(2023,2,15,12,0);
    final private LocalDateTime date2End = LocalDateTime.of(2023,6,15,12,0);
    final private LocalDateTime date3Start = LocalDateTime.of(2022,6,10,12,0);
    final private LocalDateTime date3End = LocalDateTime.of(2022,12,10,12,0);
    final private LocalDateTime date4Start = LocalDateTime.of(2023,1,1,12,0);
    final private LocalDateTime date4End = LocalDateTime.of(2023,12,31,12,0);
    final private LocalDateTime date5Start = LocalDateTime.of(2022,8,1,12,0);
    final private LocalDateTime date5End = LocalDateTime.of(2022,8,31,12,0);

    Member joe = new Member("Joe.Doe@example.com", "Joe", "Doe", null, true);
    Member hanna = new Member("Hanna.Montana@example.com", "Hannah", "Montana", null, false);
    Member god = new Member("God.Rick@example.com", "God", "Rick", null, false);
    Member jack = new Member("Jack.Sparrow@example.com", "Jack", "Sparrow", null, false);
    Member anakin = new Member("Anakin.Skywalker@example.com", "Anakin", "Skywalker", null, false);

    Image pic1 = new Image("Hustle", "image/jpg", type1);
    Image pic2 = new Image("Bustle", "image/jpg", type2);
    Image pic3 = new Image("Rad", "image/jpg", type3);
    Image pic4 = new Image("Kanoo", "image/jpg", type4);
    Image pic5 = new Image("Skateboard", "image/jpg", type5);

    Challenge hustle = new Challenge("Hustle", "Welches Team kann mehr Km an 3 Tagen zurücklegen?", date1Start, date1End, pic1, 2000);
    Challenge bustle = new Challenge("Bustle", "Welches Team erreicht das Ziel näher während dem Ausflug?", date2Start, date2End, pic2, 1000);
    Challenge anniversary = new Challenge("Anniversary", "Zum Jahrestag gibt es eine weitere Challenge. Sieger erhalten ein Essen auf Kosten der Verlierer!", date3Start, date3End, pic3, 5000);
    Challenge one = new Challenge("One-Year-Challenge", "Welches Team schafft die meisten Km über das Jahr?", date4Start, date4End, pic4, 15000);
    Challenge summer = new Challenge("Summer fun!", "Auch im Sommer ist Bewegung wichtig! Holt euch die Kilometer in der Sonne!", date5Start, date5End, pic5, 2500);

    Sport laufen = new Sport("Laufen", 3);
    Sport radfahren = new Sport("Radfahren",1);
    Sport pogo = new Sport("Pogohüpfen", 5);
    Sport kanoo = new Sport("Kanoofahren", 6);
    Sport skaten = new Sport("Skaten", 2);

    ChallengeSport cp1 = new ChallengeSport(3, hustle, laufen);
    ChallengeSport cp2 = new ChallengeSport(2, bustle, radfahren);
    ChallengeSport cp3 = new ChallengeSport(4, anniversary, pogo);
    ChallengeSport cp4 = new ChallengeSport(1, one, skaten);
    ChallengeSport cp5 = new ChallengeSport(2, summer, kanoo);

    Team red = new Team("Team Red", pic1, hustle);
    Team RedHater = new Team("Team RedHater", pic1, hustle);
    Team blue = new Team("Team Blue", pic2, bustle);
    Team besten = new Team("Die Besten", pic3, anniversary);
    Team anderen = new Team("Die Anderen", pic4, one);
    Team musketiere = new Team("Die drei Musketiere und ihre Handlanger", pic5, summer);

    TeamMember tm1 = new TeamMember(red, joe);
    TeamMember tm2 = new TeamMember(blue, hanna);
    TeamMember tm3 = new TeamMember(besten, god);
    TeamMember tm4 = new TeamMember(anderen, jack);
    TeamMember tm5 = new TeamMember(musketiere, anakin);
    TeamMember tm6 = new TeamMember(red, hanna);
    TeamMember tm7 = new TeamMember(red, god);
    TeamMember tm8 = new TeamMember( RedHater, jack);
    TeamMember tm9 = new TeamMember( RedHater, anakin);

    Activity act1 = new Activity(cp1, joe, 4, date1Start);
    Activity act2 = new Activity(cp2, hanna, 3, date2Start);
    Activity act3 = new Activity(cp3, god, 5, date3Start);
    Activity act4 = new Activity(cp4, jack, 7, date4Start);
    Activity act5 = new Activity(cp5, anakin, 3, date5Start);

    Bonus doub = new Bonus( date1Start, date1End, 2, "DoubleXP Weekend", "Doppelte Kilomete übers Wochenende");
    Bonus anni = new Bonus( date2Start, date2End, 3, "Anniversary", "Wegen Anniversary gibt es mehr Kilometer!");
    Bonus holi = new Bonus( date3Start, date3End, 4, "Holiday Event", "Während den Ferien gibt es mehr KM!");
    Bonus finish = new Bonus( date4Start, date4End, 2, "Finished Project", "Aufgrund des beendeten Projekts gibt es mehr Kilometeer für alle!");
    Bonus lucky = new Bonus( date5Start, date5End, 3, "Lucky Day!", "Für heute gibt es mehr Kilometer!");


    ChallengeSportBonus doubl = new ChallengeSportBonus(cp1,doub);
    ChallengeSportBonus anniv = new ChallengeSportBonus(cp2,anni);
    ChallengeSportBonus holid = new ChallengeSportBonus(cp3,holi);
    ChallengeSportBonus finishe = new ChallengeSportBonus(cp4,finish);
    ChallengeSportBonus luckys = new ChallengeSportBonus(cp5,lucky);
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
                joe, hanna, god, jack, anakin
        ));

        imageRepository.saveAll(Arrays.asList(
                pic1, pic2, pic3, pic4, pic5
        ));

        challengeRepository.saveAll(Arrays.asList(
                hustle, bustle, anniversary, one, summer
        ));

        sportRepository.saveAll(Arrays.asList(
                laufen, radfahren, pogo, kanoo, skaten
        ));

        challengeSportRepository.saveAll(Arrays.asList(
                cp1, cp2, cp3, cp4, cp5
        ));

        teamRepository.saveAll(Arrays.asList(
                red, RedHater, blue, besten, anderen, musketiere
        ));

        teamMemberRepository.saveAll(Arrays.asList(
                tm1, tm2, tm3, tm4, tm5, tm6, tm7, tm8, tm9
        ));

        activityRepository.saveAll(Arrays.asList(
                act1, act2, act3, act4, act5
        ));

        bonusRepository.saveAll(Arrays.asList(
                doub, anni, holi, finish, lucky
        ));
        challengeSportBonusRepository.saveAll(Arrays.asList(
                doubl, anniv, holid, finishe, luckys
        ));

        activityService.totalDistanceAll();
    }
}

