import { Component } from "react";
import GlobalVariables from "../../GlobalVariables";

/**
* Creates a new sports row and returns it
* @author Jason Patrick Duffy
*/
class ChallengeSportsRow extends Component{
    constructor(props) {
        super(props);

        this.state = {
            challengeID: props.challengeID,
            sport: props.sport,
            factor: 0
        };
    }

    async componentDidMount(){
        let effFactor = await fetch(GlobalVariables.serverURL + "/challenges/" + this.state.challengeID + "/sports/" + this.state.sport.id + "/effective/", { method: "GET", credentials: "include" });
        let effFactorData = await effFactor.text();
        this.setState({ factor: parseFloat(effFactorData).toFixed(2) });
    }

    render(){
        return (
            <tr key={"sport " + this.state.sport.name}>
                <td>
                    {this.state.sport.name}
                </td>
                <td>
                    {parseFloat(this.state.sport.factor).toFixed(1)}
                </td>
                <td>
                    {this.state.factor}
                </td>
            </tr>
        );
    }
}

export default ChallengeSportsRow;