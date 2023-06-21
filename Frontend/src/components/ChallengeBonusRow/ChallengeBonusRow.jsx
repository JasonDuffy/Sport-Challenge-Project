import { Component } from "react";
import GlobalVariables from "../../GlobalVariables";
import { Link } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faMedal, faPencil } from "@fortawesome/free-solid-svg-icons";

/**
* Creates a new table row and returns it
* @author Jason Patrick Duffy
*/
class ChallengeBonusRow extends Component {
    constructor(props) {
        super(props);

        this.state = {
            type: props.type,
            bonus: props.bonus,
            sportsString: ""
        };
    }

    async componentDidMount() {
        let sports = await fetch(GlobalVariables.serverURL + "/bonuses/" + this.state.bonus.id + "/sports/", { method: "GET", credentials: "include" });
        let sportsData = await sports.json();

        if (sportsData.length > 0) {
            let localSportsString = sportsData.at(0).name;
            for (let i = 1; i < sportsData.length; i++) {
                localSportsString += ", " + sportsData.at(i).name;
            }
            this.setState({ sportsString: localSportsString });
        }
    }

    render() {
        return (
            <tr key={"bonus" + this.state.bonus.id}>
                <td>
                    {this.state.bonus.name}
                </td>
                <td>
                    {this.state.bonus.description}
                </td>
                <td>
                    {this.state.sportsString}
                </td>
                <td>
                    {this.state.bonus.startDate}
                </td>
                <td>
                    {this.state.bonus.endDate}
                </td>
                <td>
                    {this.state.bonus.factor}
                </td>
                {this.state.type === "current" && (
                    <td>
                        <div className="row_edit_icon icon_faPencil">
                            <Link to="/bonus/edit" state={{ bonusID: this.state.bonus.id }}>
                                <FontAwesomeIcon icon={faPencil} />
                            </Link>
                        </div>
                    </td>
                )}
            </tr>
        );
    }
}

export default ChallengeBonusRow;