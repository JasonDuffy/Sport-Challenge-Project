import React, { Component } from "react";
import ChallengeTeamPanel from "./ChallengeTeamPanel";
import withRouter from "./withRouter";

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
            challengeLoaded: false
        };
    }

    async componentDidMount() {
        let challenge = await fetch("http://localhost:8081/challenges/" + this.props.params.id + "/", { method: "GET", credentials: "include" });
        let challengeResData = await challenge.json();

        this.setState({ challenge: challengeResData }, () => {
            this.setState({ challengeLoaded: true });
        });

        const pageLoading = document.getElementById("page_loading");
        pageLoading.parentNode.removeChild(pageLoading);
        document.getElementById("page").style.display = "block";
    }

    render() {
        if(!this.state.challengeLoaded)
            return;

        return (
            <section className="section_container">
                <div className="section_content">
                    <div className="">
                        < ChallengeTeamPanel key="teamPanel" id={1} width="100%" height={400} lineColor="#00ff00" fillColor="#ff0000" />
                    </div>
                </div>
            </section>

        );
    }
}
export default withRouter(Challenge);