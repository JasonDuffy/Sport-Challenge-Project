import React, { Component } from "react";
import ChallengeTeamPanelAreaGraph from "./ChallengeTeamPanelAreaGraph";

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
            teamActivities: [],
            hasLoaded: false
        };
    }

    async componentDidMount() {
        let activities = await fetch("http://localhost:8081/teams/" + this.props.id + "/activities/", { method: "GET", credentials: "include" });
        let activitiesResData = await activities.json();

        this.setState({ teamActivities: activitiesResData }, () => {
            this.setState({ hasLoaded: true });
        });
    }

    render() {
        if (!this.state.hasLoaded) {
            return;
        }

        return (
            <div width={this.state.width} height={this.state.height}>
                <ChallengeTeamPanelAreaGraph key={"area" + this.state.teamID} id={this.state.teamID} activities={this.state.teamActivities} width="50%" height={this.state.height * 0.4} lineColor="#32547B" fillColor="#D7E9F4" />
            </div>
        );
    }
}
export default ChallengeTeamPanel;