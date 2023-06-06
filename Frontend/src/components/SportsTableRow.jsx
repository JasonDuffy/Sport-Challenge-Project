import React, { Component } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPencil, faXmark } from "@fortawesome/free-solid-svg-icons";
import "./css/MyChallengesTableRow.css";
import "./css/Form.css";

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
    const sportsResponse = await fetch("http://localhost:8081/sports/" + this.props.id + "/", { method: "DELETE", credentials: "include" });
    if (sportsResponse.ok) {
      this.setState({ deleted: true });
    }
  }

  async componentDidMount() {
    let sportsResponse = await fetch("http://localhost:8081/sports/" + this.props.id + "/", { method: "GET", credentials: "include" });
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
        <td>{this.state.sportsFactor}</td>
        <td>
          <div className="row_edit_icon icon_faPencil">
            <a href={'Edit/Sport/' + this.props.id} style={{ color: "#ffeeee"}}>
              <FontAwesomeIcon icon={faPencil} />
            </a>
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
