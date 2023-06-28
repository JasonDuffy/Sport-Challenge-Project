import React, { Component } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faMedal } from "@fortawesome/free-solid-svg-icons";

/**
 * Generates the table for the team panel
 * @author Jason Patrick Duffy
 */
class ChallengeTeamPanelTable extends Component {
    constructor(props) {
        super(props);

        this.state = {
            teamID: props.id,
            activities: props.activities,
            members: props.members
        };

        this.memberTable = this.memberTable.bind(this);
    }

    /**
     * Decides if a medal or the position should be displayed
     * @param position Position for which the decision should be taken
     * @returns Medal or position
     */
    medalDecider(position) {
        switch (position) {
            case 1:
                return (<FontAwesomeIcon icon={faMedal} color="gold" />);
            case 2:
                return (<FontAwesomeIcon icon={faMedal} color="silver" />);
            case 3:
                return (<FontAwesomeIcon icon={faMedal} color="#cd7f32" />);
            default:
                return position;
        }
    }

    /**
     * Creates a new table row and returns it
     * @param member Member object of MemberDTO form
     * @returns New table row
     */
    rowMaker(position, member) {
        return (
            <tr key={position}>
                <td>
                    {this.medalDecider(position)}
                </td>
                <td>
                    {member.name}
                </td>
                <td>
                    {member.totalDistance}
                </td>
            </tr>
        );
    }

    /**
     * Returns a bar graph with a bar for each team member showing their current points
     */
    memberTable() {
        let data = this.state.activities;
        let members = this.state.members;

        // Data can only be properly formatted when at least 1 data point exists

        const formattedDataArray = []; // Contains the data for the graph

        // Format data for correct display
        while (data.length !== 0) {
            const formattedData = {}; // Contains the data for a single point on the graph

            const element = data.at(0); // Get first element

            // Get all elements with same date as first element
            const filteredObjects = data.filter(obj => obj.memberID === element.memberID);

            // Sum for totalDistance calculation
            let sum = 0;

            // Add all totalDistances of this day to sum
            for (const el of filteredObjects) {
                sum += el.totalDistance;
            }

            // Get member with same same id out of array
            const filteredMember = members.filter(obj => obj.userID === element.memberID);

            // Set name for table
            formattedData.name = filteredMember.at(0).firstName + " " + filteredMember.at(0).lastName;

            // Set totalDistance of this day to sum
            formattedData.totalDistance = sum;

            // Remove all data from this day from data array
            data = data.filter(obj => obj.memberID !== element.memberID);
            // Remove member from member array
            members = members.filter(obj => obj.userID !== element.memberID);

            // Push data to array
            formattedDataArray.push(formattedData);
        }

        // Add members without any activities to table
        for(const element of members){
            const formattedData = {};

            formattedData.name = element.firstName + " " + element.lastName;
            formattedData.totalDistance = 0;

            formattedDataArray.push(formattedData);
        }

        formattedDataArray.sort(function (a, b) {
            return b.totalDistance - a.totalDistance;
        });

        // Counter for the position display
        let position = 1;

        return (
            <div className="mg_t_1">
                <table className="last_activites_table">
                    <thead>
                        <tr>
                            <th>Position</th>
                            <th>Name</th>
                            <th>Punkte</th>
                        </tr>
                    </thead>
                    <tbody>
                        {formattedDataArray.map((item) => (
                            this.rowMaker(position++, item)
                        ))}
                        {formattedDataArray.length === 0 && (
                            <td colSpan={3}>
                                <span>Das Team besitzt aktuell keine Mitglieder.</span>
                            </td>
                        )}
                    </tbody>
                </table>
            </div>

        );
    }

    render() {
        return (
            <this.memberTable />
        );
    }
}
export default ChallengeTeamPanelTable;