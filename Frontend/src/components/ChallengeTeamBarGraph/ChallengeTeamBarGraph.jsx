import React, { Component } from "react";
import { CartesianGrid, XAxis, YAxis, Tooltip, ResponsiveContainer, BarChart, Bar } from 'recharts';

/**
 * Generates the bar graph for a set of teams
 * @author Jason Patrick Duffy
 */

class ChallengeTeamBarGraph extends Component {
    constructor(props) {
        super(props);

        this.state = {
            width: props.width,
            aspect: props.aspect,
            lineColor: props.lineColor,
            fillColor: props.fillColor,
            teams: props.teams
        };

        this.pointsBarGraph = this.pointsBarGraph.bind(this);
    }

    /**
     * Creates and returns a bar graph showing the teams and their covered average distance
     * @returns Bar graph in ResponsiveContainer
     */
    pointsBarGraph() {
        let teams = structuredClone(this.state.teams);

        // Data can only be properly formatted when at least 1 data point exists
        if (teams.length > 0) {
            const formattedDataArray = []; // Contains the data for the graph

            // Add members without any activities to graph
            for (const element of teams) {
                const formattedData = {}; // Contains the data for a single point on the graph

                // Set name for bar chart
                formattedData.name = element.name;

                // Set totalDistance of this day to sum
                formattedData.avgDistance = element.avgDistance;

                // Push day data to array
                formattedDataArray.push(formattedData);
            }

            // Sort data for nicer display
            formattedDataArray.sort(function (a, b) {
                return b.avgDistance - a.avgDistance;
            });

            return (
                <ResponsiveContainer width={this.state.width} aspect={this.state.aspect}>
                    <BarChart data={formattedDataArray}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="name" interval="preserveStartEnd" />
                        <YAxis />
                        <Tooltip />
                        <Bar type="monotone" dataKey="avgDistance" name="Durchschnittliche Punkte" stroke={this.state.lineColor} fill={this.state.fillColor} />
                    </BarChart>
                </ResponsiveContainer>

            );
        }
        else {
            return;
        }
    }

    render() {
        return (
            <this.pointsBarGraph />
        );
    }
}
export default ChallengeTeamBarGraph;