import React, { Component } from "react";
import { CartesianGrid, XAxis, YAxis, Tooltip, ResponsiveContainer, BarChart, Bar } from 'recharts';

/**
 * Generates the area graph for a set of given activities
 * @author Jason Patrick Duffy
 */

class ChallengeTeamPanelBarGraph extends Component {
    constructor(props) {
        super(props);

        this.state = {
            teamID: props.id,
            width: props.width,
            aspect: props.aspect,
            lineColor: props.lineColor,
            fillColor: props.fillColor,
            activities: props.activities,
            members: props.members
        };

        this.pointsBarGraph = this.pointsBarGraph.bind(this);
    }

    /**
     * Returns a bar graph with a bar for each team member showing their current points
     */
    pointsBarGraph() {
        let data = this.state.activities;
        let members = this.state.members;

        // Data can only be properly formatted when at least 1 data point exists
        if (data.length > 0) {
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

                // Set name for bar chart
                formattedData.name = filteredMember.at(0).firstName + " " + filteredMember.at(0).lastName;

                // Set totalDistance of this day to sum
                formattedData.totalDistance = sum;

                // Remove all data from this day from data array
                data = data.filter(obj => obj.memberID !== element.memberID);
                // Remove member from member array
                members = members.filter(obj => obj.userID !== element.memberID);

                // Push day data to array
                formattedDataArray.push(formattedData);
            }

            // Add members without any activities to graph
            for (const element of members) {
                const formattedData = {}; // Contains the data for a single point on the graph

                // Set name for bar chart
                formattedData.name = element.firstName + " " + element.lastName;

                // Set totalDistance of this day to sum
                formattedData.totalDistance = 0;

                // Push day data to array
                formattedDataArray.push(formattedData);
            }

            // Sort data for nicer display
            formattedDataArray.sort(function (a, b) {
                return b.totalDistance - a.totalDistance;
            });

            return (
                <ResponsiveContainer width={this.state.width} aspect={this.state.aspect}>
                    <BarChart data={formattedDataArray}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="name" interval="preserveStartEnd"/>
                        <YAxis />
                        <Tooltip />
                        <Bar type="monotone" dataKey="totalDistance" name="Insgesamt gesammelte Punkte" stroke={this.state.lineColor} fill={this.state.fillColor} />
                    </BarChart>
                </ResponsiveContainer>

            );
        }
        else {
            return (
                <ResponsiveContainer width={this.state.width} aspect={this.state.aspect}>
                    <BarChart data={data}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="dateString" interval="preserveStartEnd" />
                        <YAxis />
                        <Tooltip />
                        <Bar type="monotone" dataKey="totalDistance" name="Insgesamt gesammelte Punkte" stroke={this.state.lineColor} fill={this.state.fillColor} />
                    </BarChart>
                </ResponsiveContainer>
            );
        }
    }

    render() {
        return (
            <this.pointsBarGraph />
        );
    }
}
export default ChallengeTeamPanelBarGraph;