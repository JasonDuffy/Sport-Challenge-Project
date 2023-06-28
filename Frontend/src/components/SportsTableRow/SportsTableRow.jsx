import React, { Component } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPencil, faXmark } from "@fortawesome/free-solid-svg-icons";
import GlobalVariables from "../../GlobalVariables.js"
import "./SportsTableRow.css";
import { Link } from "react-router-dom";

/**
 * @author Jason Patrick Duffy
 */

class SportsTableRow extends Component {
  constructor(props) {
    super(props);

    this.state = {
      sportsName: "",
      sportsFactor: 0.0,
      deleted: false
    };

    this.deleteRow = this.deleteRow.bind(this);
  }

  async deleteRow(event) {
    if(window.confirm("Möchtest du die Sportart " + this.state.sportsName + " wirklich löschen?") === true){
      const sportsResponse = await fetch(GlobalVariables.serverURL + "/sports/" + this.props.id + "/", { method: "DELETE", credentials: "include" });
      if (sportsResponse.ok) {
        this.setState({ deleted: true });
      }
    }
  }

  async componentDidMount() {
    let sportsResponse = await fetch(GlobalVariables.serverURL + "/sports/" + this.props.id + "/", { method: "GET", credentials: "include" });
    let sportsResData = await sportsResponse.json();

    this.setState({ sportsName: sportsResData.name });
    this.setState({ sportsFactor: sportsResData.factor });
  }

  render() {
    if (this.state.deleted === true) {
      return;
    }
    return (
      <tr>
        <td>{this.state.sportsName}</td>
        <td>{this.state.sportsFactor.toFixed(2)}</td>
        <td>
          <div className="row_edit_icon icon_faPencil">
            <Link to="/sport/edit" state={{id: this.props.id}}>
              <FontAwesomeIcon icon={faPencil} />
            </Link>
          </div>
          <div className="row_edit_icon icon_faXmark" onClick={this.deleteRow}>
            <FontAwesomeIcon icon={faXmark} size="lg" />
          </div>
        </td>
      </tr>
    );
  }
}

export default SportsTableRow;
