import React, { Component } from "react";
import ChallengeTeamPanelAreaGraph from "./ChallengeTeamPanelAreaGraph";
import ChallengeTeamPanelBarGraph from "./ChallengeTeamPanelBarGraph";
import ChallengeTeamPanelTable from "./ChallengeTeamPanelTable";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPencil, faXmark } from "@fortawesome/free-solid-svg-icons";
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
            challenge: props.challenge,
            activitiesLoaded: false,
            membersLoaded: false,
            teamLoaded: false
        };
    }

    // Needed for team change
    componentDidUpdate(prevProps, prevState) {
        if (prevProps.id !== this.props.id) {
            this.setState({ activitiesLoaded: false }, () => {
                this.setState({ membersLoaded: false }, () => {
                    this.setState({ teamLoaded: false }, () => {
                        this.load();
                    });
                });
            });
        }
    }

    componentDidMount() {
        this.load();
    }

    async load() {
        if (this.state.teamID !== 0) {
            // ACTIVITIES FETCH --------------------------------------
            let activities = await fetch("http://localhost:8081/teams/" + this.props.id + "/activities/", { method: "GET", credentials: "include" });
            let activitiesResData = await activities.json();

            this.setState({ teamActivities: activitiesResData }, () => {
                this.setState({ activitiesLoaded: true });
            });

            // MEMBERS FETCH --------------------------------------
            let members = await fetch("http://localhost:8081/teams/" + this.props.id + "/members/", { method: "GET", credentials: "include" });
            let memberData = await members.json();

            this.setState({ teamMembers: memberData }, () => {
                this.setState({ membersLoaded: true });
            });

            // TEAM FETCH --------------------------------------
            let team = await fetch("http://localhost:8081/teams/" + this.props.id + "/", { method: "GET", credentials: "include" });
            let teamData = await team.json();

            this.setState({ teamData: teamData }, () => {
                this.setState({ teamLoaded: true });
            });
        }
    }

    render() {
        if (!this.state.activitiesLoaded || !this.state.membersLoaded || !this.state.teamLoaded) {
            return;
        }

        return (
            <div>
                <div className="heading_underline_center">
                    <span className="underline_center" id="headingText">
                        {this.state.teamData.name}
                    </span>
                </div>
                <div className="center_content mg_t_5">
                    <div className="row_edit_icon icon_faPencil">
                        <a href={'../Edit/Team/' + this.props.id} style={{ color: "#ffeeee"}}>
                            <FontAwesomeIcon icon={faPencil} />
                        </a>
                    </div>
                </div>
                <div className="graphContainer">
                    <ChallengeTeamPanelAreaGraph key={"area" + this.state.teamID}
                        activities={structuredClone(this.state.teamActivities)}
                        width="50%" aspect={1} lineColor="#C63328" fillColor="#ff9f00"
                        endDate={structuredClone(this.state.challenge.endDate)} />

                    <ChallengeTeamPanelBarGraph key={"bar" + this.state.teamID}
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