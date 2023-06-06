import React, { Component } from "react";
import ChallengeTeamPanelAreaGraph from "./ChallengeTeamPanelAreaGraph";
import ChallengeTeamPanelBarGraph from "./ChallengeTeamPanelBarGraph";
import ChallengeTeamPanelTable from "./ChallengeTeamPanelTable";
import "./css/ChallengeTeamPanelGraphs.css";

/**
 * Provides the team panel for the challenge page
 * @author Jason Patrick Duffy
 */

class ChallengeTeamPanel extends Component {
    constructor(props) {
        super(props);

        this.state = {
            teamID: props.id,
            width: props.width,
            height: props.height,
            teamData: [],
            teamActivities: [],
            teamMembers: [],
            activitiesLoaded: false,
            membersLoaded: false,
            teamLoaded: false
        };
    }

    async componentDidMount() {
        let activities = await fetch("http://localhost:8081/teams/" + this.props.id + "/activities/", { method: "GET", credentials: "include" });
        let activitiesResData = await activities.json();

        this.setState({ teamActivities: activitiesResData }, () => {
            this.setState({ activitiesLoaded: true });
        });

        let members = await fetch("http://localhost:8081/teams/" + this.props.id + "/members/", { method: "GET", credentials: "include" });
        let memberData = await members.json();

        this.setState({ teamMembers: memberData }, () => {
            this.setState({ membersLoaded: true });
        });

        let team = await fetch("http://localhost:8081/teams/" + this.props.id + "/", { method: "GET", credentials: "include" });
        let teamData = await team.json();

        this.setState({ teamData: teamData }, () => {
            this.setState({ teamLoaded: true });
        });
    }

    render() {
        if (!this.state.activitiesLoaded || !this.state.membersLoaded || !this.state.teamLoaded) {
            return;
        }

        return (
            <div height={this.state.height} width={this.state.height}>
                <div className="section_container">
                    <div className="section_content">
                        <div className="heading_underline_center">
                            <span className="underline_center" id="headingText">
                                {this.state.teamData.name}
                            </span>
                        </div>
                        <div className="center_content mg_t_5">
                            HIER MUSS WAHRSCHEINLICH NOCH DAS TEAMBILD UND EIN KNOPF ZUM TEAM BEARBEITEN HIN
                        </div>
                    </div>
                </div>
                <div className="graphContainer">
                    <ChallengeTeamPanelAreaGraph key={"area" + this.state.teamID} id={this.state.teamID}
                        activities={structuredClone(this.state.teamActivities)}
                        width="50%" aspect={1} lineColor="#C63328" fillColor="#ff9f00" />

                    <ChallengeTeamPanelBarGraph key={"bar" + this.state.teamID} id={this.state.teamID}
                        activities={structuredClone(this.state.teamActivities)}
                        members={structuredClone(this.state.teamMembers)}
                        width="50%" aspect={1} lineColor="#C63328" fillColor="#ff9f00" />
                </div>

                <div className="otherContainer">
                    <ChallengeTeamPanelTable key={"table" + this.state.teamID} id={this.state.teamID}
                        activities={structuredClone(this.state.teamActivities)}
                        members={structuredClone(this.state.teamMembers)} />
                </div>
            </div>
        );
    }
}
export default ChallengeTeamPanel;