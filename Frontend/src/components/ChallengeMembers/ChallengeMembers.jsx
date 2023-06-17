import React, { Component } from "react";
import "./ChallengeMembers.css"
import GlobalVariables from "../../GlobalVariables.js"

/**
 * Provides a single team member in a div
 * @author Jason Patrick Duffy
 */

class ChallengeMembers extends Component {
    constructor(props) {
        super(props);

        this.state = {
            team: props.team,
            image: "",
            imageLoaded: false
        };
    }

    async componentDidMount() {
        try{
            let teamImage = await fetch(GlobalVariables.serverURL + "/images/" + this.state.team.imageID + "/", { method: "GET", credentials: "include" });
            let teamImageResData = await teamImage.json();

            this.setState({ image: "data:" + teamImageResData.type + ";base64, " + teamImageResData.data }, () => {
                this.setState({ imageLoaded: true });
            })
        } catch (error){ // Handle teams without image
            console.log("Team " + this.state.team.name + " does not have an image.");
            this.setState({ image: require(`../../assets/images/Default-Team.png`) }, () => {
                this.setState({ imageLoaded: true });
            })
        }
    }

    render() {
        if(!this.state.imageLoaded)
            return;

        return (
            <div className="teamContainer">
                <div className="teamContainerImage">
                    <img src={this.state.image} alt={"Bild des Teams " + this.state.team.name} className="challengeImage"></img>
                </div>
                <div className="teamContainerName">
                    {this.state.team.name}
                </div>
            </div>
        );
    }
}
export default ChallengeMembers;