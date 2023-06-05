import React, { Component } from "react";
import ChallengeTeamPanel from "./ChallengeTeamPanel";
import withRouter from "./withRouter";

class Challenge extends Component {
    constructor(props) {
        super(props);
    }

    componentDidMount() {
        const pageLoading = document.getElementById("page_loading");
        pageLoading.parentNode.removeChild(pageLoading);
        document.getElementById("page").style.display = "block";
    }

    render() {
        return (
            <div className="mg_t_8 width_100">
                < ChallengeTeamPanel key={1} id={1} width="100%" height={800} lineColor="#00ff00" fillColor="#ff0000" />
            </div>
        );
    }
}
export default withRouter(Challenge);