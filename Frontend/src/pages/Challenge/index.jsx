import React, { Component } from "react";
import ChallengeTeamPanel from "../../components/ChallengeTeamPanel/ChallengeTeamPanel";
import withRouter from "../withRouter";
import "./Challenge.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faMedal, faPencil } from "@fortawesome/free-solid-svg-icons";
import ChallengeTeamPanelAreaGraph from "../../components/ChallengeTeamPanelAreaGraph/ChallengeTeamPanelAreaGraph";
import ChallengeTeamBarGraph from "../../components/ChallengeTeamBarGraph/ChallengeTeamBarGraph";
import ChallengeMembers from "../../components/ChallengeMembers/ChallengeMembers";
import Button from "../../components/ui/button/Button";

/**
 * Displays all information for a given challenge
 * @author Jason Patrick Duffy, Robin Hackh
 */
class Challenge extends Component {
    constructor(props) {
        super(props);

        this.state = {
            challengeID: props.params.id,
            challenge: [],
            distance: 0,
            activities: [],
            pastBonuses: [],
            currentBonuses: [],
            futureBonuses: [],
            teams: [],
            sports: [],
            image: "",
            teamID: 0,
            challengeLoaded: false,
            teamsLoaded: false,
            imageLoaded: false,
            activitiesLoaded: false,
            pastBonusesLoaded: false,
            currentBonusesLoaded: false,
            futureBonusesLoaded: false,
            sportsLoaded: false
        };

        this.teamTableMaker = this.teamTableMaker.bind(this);
        this.bonusTableMaker = this.bonusTableMaker.bind(this);
        this.distanceDisplay = this.distanceDisplay.bind(this);

        this.teamChange = this.teamChange.bind(this);
    }

    async componentDidMount() {
        // CHALLENGE FETCH --------------------------------------
        let challenge = await fetch("http://localhost:8081/challenges/" + this.props.params.id + "/", { method: "GET", credentials: "include" });
        let challengeResData = await challenge.json();

        // Convert challenge dates to usable dates
        // Regex for date conversion 
        const dateRegex = "(\\d{2})\\.(\\d{2})\\.(\\d{4})\\,(\\d{2})\\:(\\d{2})";

        // Convert string date into JS date and add correctly formatted string to object
        let date = challengeResData.startDate.match(dateRegex);
        challengeResData.startDate = new Date(date[3], date[2] - 1, date[1]); // Only date not time needed
        date = challengeResData.endDate.match(dateRegex);
        challengeResData.endDate = new Date(date[3], date[2] - 1, date[1]); // Only date not time needed

        this.setState({ challenge: challengeResData }, () => {
            this.setState({ challengeLoaded: true });
        });

        // TEAMS FETCH --------------------------------------
        let teams = await fetch("http://localhost:8081/challenges/" + this.props.params.id + "/teams/", { method: "GET", credentials: "include" });
        let teamsResData = await teams.json();

        // Add avgDistance to each team
        if (teamsResData.length > 0) {
            for (const team of teamsResData) {
                let avg = await fetch("http://localhost:8081/teams/" + team.id + "/avgDistance/", { method: "GET", credentials: "include" });
                let avgResData = await avg.text();
                team.avgDistance = parseFloat(avgResData).toFixed(2);
                if (isNaN(team.avgDistance)) {
                    team.avgDistance = (0).toFixed(2);
                }
            }

            this.setState({ teams: teamsResData }, () => {
                this.setState({ teamID: teamsResData.at(0).id }, () => {
                    this.setState({ teamsLoaded: true });
                });
            });
        } else {
            this.setState({ teamsLoaded: true });
        }

        // CHALLENGE IMAGE FETCH --------------------------------------
        let image = await fetch("http://localhost:8081/images/" + this.state.challenge.imageID + "/", { method: "GET", credentials: "include" });
        let imageResData = await image.json();

        this.setState({ image: "data:" + imageResData.type + ";base64, " + imageResData.data }, () => {
            this.setState({ imageLoaded: true });
        });

        // ACTIVITIES FETCH --------------------------------------
        let challengeActivities = await fetch("http://localhost:8081/challenges/" + this.props.params.id + "/activities/", { method: "GET", credentials: "include" });
        let challengeActivitiesResData = await challengeActivities.json();

        this.setState({ activities: challengeActivitiesResData }, () => {
            this.setState({ activitiesLoaded: true });
        });

        // PAST BONUSES FETCH --------------------------------------
        let past = await fetch("http://localhost:8081/challenges/" + this.props.params.id + "/bonuses/?type=past", { method: "GET", credentials: "include" });
        let pastResData = await past.json();

        this.setState({ pastBonuses: pastResData }, () => {
            this.setState({ pastBonusesLoaded: true });
        });

        // CURRENT BONUSES FETCH --------------------------------------
        let current = await fetch("http://localhost:8081/challenges/" + this.props.params.id + "/bonuses/?type=current", { method: "GET", credentials: "include" });
        let currentResData = await current.json();

        this.setState({ currentBonuses: currentResData }, () => {
            this.setState({ currentBonusesLoaded: true });
        });

        // FUTURE BONUSES FETCH --------------------------------------
        let future = await fetch("http://localhost:8081/challenges/" + this.props.params.id + "/bonuses/?type=future", { method: "GET", credentials: "include" });
        let futureResData = await future.json();

        this.setState({ futureBonuses: futureResData }, () => {
            this.setState({ futureBonusesLoaded: true });
        });

        // CHALLENGE SPORTS FETCH --------------------------------------
        let sports = await fetch("http://localhost:8081/challenges/" + this.props.params.id + "/sports/", { method: "GET", credentials: "include" });
        let sportsResData = await sports.json();

        this.setState({ sports: sportsResData }, () => {
            this.setState({ sportsLoaded: true });
        });

        // CHALLENGE DISTANCE FETCH --------------------------------------
        let challengeDistance = await fetch("http://localhost:8081/challenges/" + this.props.params.id + "/distance/", { method: "GET", credentials: "include" });
        let challengeDistanceResData = await challengeDistance.json();

        this.setState({ distance: challengeDistanceResData });

        // REMOVE LOADING ICON  --------------------------------------
        const pageLoading = document.getElementById("page_loading");
        pageLoading.parentNode.removeChild(pageLoading);
        document.getElementById("page").style.display = "block";
    }

    /**
    * Decides if a medal or the position should be displayed
    * @param position Position for which the decision should be taken
    * @returns Medal or position
    */
    medalDecider(position) {
        switch (position) {
            case 1:
                return (<FontAwesomeIcon icon={faMedal} color="gold" />);
            case 2:
                return (<FontAwesomeIcon icon={faMedal} color="silver" />);
            case 3:
                return (<FontAwesomeIcon icon={faMedal} color="#cd7f32" />);
            default:
                return position;
        }
    }

    /**
     * Creates a new table row and returns it
     * @param position Leaderboard position of the team 
     * @param team Team DTO object with added avgDistance 
     * @returns New table row
     */
    teamRowMaker(position, team) {
        return (
            <tr key={position}>
                <td>
                    {this.medalDecider(position)}
                </td>
                <td>
                    {team.name}
                </td>
                <td>
                    {team.avgDistance}
                </td>
            </tr>
        );
    }

    /**
     * Creates the team table
     * @returns Team table in div
     */
    teamTableMaker() {
        let position = 1;

        const data = structuredClone(this.state.teams);

        data.sort(function (a, b) {
            return b.avgDistance - a.avgDistance;
        });

        return (
            <div>
                <table className="last_activites_table">
                    <thead>
                        <tr>
                            <th>Position</th>
                            <th>Name</th>
                            <th>Durchschnittliche Punktzahl</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.map((item) => (
                            this.teamRowMaker(position++, item)
                        ))}
                    </tbody>
                </table>
            </div>
        );
    }

    /**
    * Creates a new table row and returns it
    * @param type Type of the bonus row. "current" adds an edit option
    * @param bonus Bonus DTO object 
    * @returns New table row
    */
    bonusRowMaker(type, bonus) {
        return (
            <tr key={"bonus" + bonus.id}>
                <td>
                    {bonus.name}
                </td>
                <td>
                    {bonus.description}
                </td>
                <td>
                    {"Sportart 1, Sportart 2, Sportart 3"}
                </td>
                <td>
                    {bonus.startDate}
                </td>
                <td>
                    {bonus.endDate}
                </td>
                <td>
                    {bonus.factor}
                </td>
                {type === "current" && (
                    <td>
                        <div className="row_edit_icon icon_faPencil">
                            <a href={'../Edit/Bonus/' + bonus.id} style={{ color: "#ffeeee" }}>
                                <FontAwesomeIcon icon={faPencil} />
                            </a>
                        </div>
                    </td>
                )}
            </tr>
        );
    }

    /**
    * Creates the bonus table
    * @returns Bonus table in div
    */
    bonusTableMaker(type) {
        if (type == "past") {
            return (
                <div>
                    <table className="last_activites_table">
                        <thead>
                            <tr>
                                <th>Bonus</th>
                                <th>Beschreibung</th>
                                <th>Sportarten</th>
                                <th>Beginn</th>
                                <th>Ende</th>
                                <th>Faktor</th>
                            </tr>
                        </thead>
                        <tbody>
                            {this.state.pastBonuses.map((item) => (
                                this.bonusRowMaker(type, item)
                            ))}
                        </tbody>
                    </table>
                </div>
            );
        } else if (type == "current") {
            return (
                <div>
                    <table className="last_activites_table">
                        <thead>
                            <tr>
                                <th>Bonus</th>
                                <th>Beschreibung</th>
                                <th>Sportarten</th>
                                <th>Beginn</th>
                                <th>Ende</th>
                                <th>Faktor</th>
                                <th>Aktion</th>
                            </tr>
                        </thead>
                        <tbody>
                            {this.state.currentBonuses.map((item) => (
                                this.bonusRowMaker(type, item)
                            ))}
                        </tbody>
                    </table>
                </div>
            );
        } else {
            return (
                <div>
                    <table className="last_activites_table">
                        <thead>
                            <tr>
                                <th>Bonus</th>
                                <th>Beschreibung</th>
                                <th>Sportarten</th>
                                <th>Beginn</th>
                                <th>Ende</th>
                                <th>Faktor</th>
                            </tr>
                        </thead>
                        <tbody>
                            {this.state.futureBonuses.map((item) => (
                                this.bonusRowMaker(type, item)
                            ))}
                        </tbody>
                    </table>
                </div>
            );
        }
    }

    /**
    * Creates a new sports row and returns it
    * @param sports Sports DTO object 
    * @returns New table row
    */
    sportsRowMaker(sports) {
        return (
            <tr key={"sport " + sports.name}>
                <td>
                    {sports.name}
                </td>
                <td>
                    {sports.factor}
                </td>
                <td>
                    {"XX"}
                </td>
            </tr>
        );
    }

    /**
     * Creates the sports table
     * @returns Sports table in div
     */
    sportTableMaker() {
        return (
            <div>
                <table className="last_activites_table">
                    <thead>
                        <tr>
                            <th>Sportart</th>
                            <th>Faktor</th>
                            <th>Effektiver Faktor gerade</th>
                        </tr>
                    </thead>
                    <tbody>
                        {this.state.sports.map((item) => (
                            this.sportsRowMaker(item)
                        ))}
                    </tbody>
                </table>
            </div>
        );
    }

    /**
     * Returns the computed distance as a formatted string
     * @returns Computed distance in string
     */
    distanceDisplay() {
        let percentage = (this.state.distance * 100 / this.state.challenge.targetDistance).toFixed(2)
        let distanceString = this.state.distance + " / " + this.state.challenge.targetDistance + "; " + percentage + "%";
        return (distanceString);
    }

    teamChange(event) {
        this.setState({ teamID: event.target.value });
    }

    render() {
        if (!this.state.challengeLoaded || !this.state.teamsLoaded ||
            !this.state.imageLoaded || !this.state.activitiesLoaded ||
            !this.state.pastBonusesLoaded || !this.state.currentBonusesLoaded || !this.state.futureBonusesLoaded ||
            !this.state.sportsLoaded)
            return;

        return (
            <div>
                <section className="section_container">
                    <div className="section_content">
                        <div className="centered mg_t_5">
                            <div className="imageContainer">
                                <div className="textContainer">
                                    <div className="challengeName">
                                        {this.state.challenge.name + " "}
                                        <a href={'../Edit/Challenge/' + this.props.params.id} style={{ color: "#ffeeee" }}>
                                            <FontAwesomeIcon icon={faPencil} />
                                        </a>
                                    </div>
                                    <div className="challengeProgress">
                                        <this.distanceDisplay />
                                    </div>
                                    <div className="challengeDescription">
                                        {this.state.challenge.description}
                                    </div>
                                </div>
                                <div className="challengeImageContainer">
                                    <div className="imageOverlay"></div>
                                    <img src={this.state.image} alt="Bild der Challenge" className="challengeImage"></img>
                                </div>
                            </div>

                            <div className="center_content mg_t_2">
                                <a href={'../Add/Team/' + this.props.params.id} style={{ color: "#ffeeee" }}>
                                    <Button color="orange" txt="Challenge beitreten" />
                                </a>
                            </div>
                        </div>
                    </div>

                    <div className="section_content">
                        <div className="heading_underline_center">
                            <span className="underline_center" id="memberText">
                                Teilnehmende Teams
                            </span>
                            <div className="mg_t_6">
                                <div className="challengeMembers">
                                    {this.state.teams.map((item) => (
                                        <ChallengeMembers key={item.id} team={structuredClone(item)} />
                                    ))}
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="section_content">
                        <div className="heading_underline_center">
                            <span className="underline_center" id="bonusText">
                                Laufende Bonusaktionen
                            </span>
                            <div className="mg_t_6">
                                {this.bonusTableMaker("current")}
                            </div>
                            <div className="center_content mg_t_2">
                                <a href={'../Add/Bonus/' + 0} style={{ color: "#ffeeee" }}>
                                    <Button color="orange" txt="Neuen Bonus erstellen" />
                                </a>
                            </div>
                        </div>
                    </div>

                    {this.state.futureBonuses.length > 0 && (
                        <div className="section_content">
                            <div className="heading_underline_center">
                                <span className="underline_center" id="futureBonusText">
                                    Zukünftige Bonusaktionen
                                </span>
                                <div className="mg_t_6">
                                    {this.bonusTableMaker("future")}
                                </div>
                            </div>
                        </div>
                    )}

                    {this.state.pastBonuses.length > 0 && (
                        <div className="section_content">
                            <div className="heading_underline_center">
                                <span className="underline_center" id="pastBonusText">
                                    Vorherige Bonusaktionen
                                </span>
                                <div className="mg_t_6">
                                    {this.bonusTableMaker("past")}
                                </div>
                            </div>
                        </div>
                    )}

                    <div className="section_content">
                        <div className="heading_underline_center">
                            <span className="underline_center" id="sportText">
                                Sportarten dieser Challenge
                            </span>
                            <div className="mg_t_6">
                                {this.sportTableMaker()}
                            </div>
                        </div>
                    </div>

                    <div className="section_content">
                        <div className="heading_underline_center mg_b_7">
                            <span className="underline_center" id="statsText">
                                Statistiken
                            </span>
                        </div>
                        <div className="teamTable">
                            <this.teamTableMaker />
                        </div>
                        <div className="graphContainer mg_t_3">
                            <ChallengeTeamPanelAreaGraph key="challengeArea"
                                activities={structuredClone(this.state.activities)}
                                width="50%" aspect={1} lineColor="#C63328" fillColor="#ff9f00"
                                startDate={structuredClone(this.state.challenge.startDate)}
                                endDate={structuredClone(this.state.challenge.endDate)} />

                            <ChallengeTeamBarGraph key={"challengeBar"}
                                teams={structuredClone(this.state.teams)}
                                width="50%" aspect={1} lineColor="#C63328" fillColor="#ff9f00" />
                        </div>
                    </div>

                    <div>
                        <form>
                            <select className="mg_t_1" value={this.state.teamID} onChange={this.teamChange}>
                                {this.state.teams.map((item) => (
                                    <option key={item.id} value={item.id}>
                                        {item.name}
                                    </option>
                                ))}
                            </select>
                        </form>
                    </div>

                    <div className="section_content">
                        <div className="teamPanel">
                            < ChallengeTeamPanel key="teamPanel"
                                id={this.state.teamID} challenge={structuredClone(this.state.challenge)}
                                width="100%" height={400} lineColor="#00ff00" fillColor="#ff0000" />
                        </div>
                    </div>
                </section>
            </div>

        );
    }
}
export default withRouter(Challenge);