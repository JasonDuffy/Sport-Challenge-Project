import React, { Component } from "react";
import { CartesianGrid, XAxis, YAxis, Tooltip, AreaChart, Area, ResponsiveContainer } from 'recharts';

/**
 * Generates the area graph for a set of given activities
 * @author Jason Patrick Duffy
 */

class ChallengeTeamPanelAreaGraph extends Component {
    constructor(props) {
        super(props);

        this.state = {
            teamID: props.id,
            width: props.width,
            aspect: props.aspect,
            lineColor: props.lineColor,
            fillColor: props.fillColor,
            activities: props.activities
        };

        this.activitiesOverTimeChart = this.activitiesOverTimeChart.bind(this);
    }

    sortDatesASC(a, b) {
        return new Date(a.date) - new Date(b.date);
    }
    sortDatesDESC(a, b) {
        return new Date(b.date) - new Date(a.date);
    }

    /**
     * Returns an area chart chronicling the progress of the given team over time
     */
    activitiesOverTimeChart() {
        let data = this.state.activities;

        // Regex for date conversion 
        const dateRegex = "(\\d{2})\\.(\\d{2})\\.(\\d{4})\\,(\\d{2})\\:(\\d{2})";

        // Convert string date into JS date and add correctly formatted string to object
        for (const element of data) {
            let date = element.date.match(dateRegex);
            element.date = new Date(date[3], date[2] - 1, date[1]); // Only date not time needed
            element.dateString = element.date.toLocaleDateString('de-de', { day: "numeric", month: "long", year: "numeric" });
        }

        // Sort ascending
        data.sort(this.sortDatesASC);

        // Data can only be properly formatted when at least 1 data point exists
        if (data.length > 0) {
            // Temp var for adding days
            let calcDate = data.at(0).date.getTime();

            // Get current day for graph generation
            let now = new Date();
            now = new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime();

            // Add empty activities for every day from start of data to today
            // Fills empty spaces and makes x-axis scale even
            while (calcDate <= now) {
                let newObj = {};
                newObj.date = new Date(calcDate);
                newObj.date.setDate(newObj.date.getDate() + 1);
                newObj.dateString = newObj.date.toLocaleDateString('de-de', { day: "numeric", month: "long", year: "numeric" });
                newObj.totalDistance = 0;

                data.push(newObj);
                calcDate += (1000 * 3600 * 24); // Adds one day in milliseconds
            }

            // Sort again
            data.sort(this.sortDatesASC);

            const formattedDataArray = []; // Contains the data for the graph

            // Format data for correct display
            while (data.length != 0) {
                const formattedData = {}; // Contains the data for a single point on the graph

                const element = data.at(0); // Get first element

                // Save string of first date in point data
                formattedData.dateString = element.dateString;

                // Get all elements with same date as first element
                const filteredObjects = data.filter(obj => obj.date.getTime() === element.date.getTime());

                // Sum for totalDistance calculation
                let sum = 0;

                // Add all totalDistance from before this point to sum
                if (formattedDataArray.length != 0) {
                    sum = formattedDataArray.at(formattedDataArray.length - 1).totalDistance;
                }

                // Add all totalDistances of this day to sum
                for (const el of filteredObjects) {
                    sum += el.totalDistance;
                }

                // Set totalDistance of this day to sum
                formattedData.totalDistance = sum;

                // Remove all data from this day from data array
                data = data.filter(obj => obj.date.getTime() !== element.date.getTime());

                // Push day data to array
                formattedDataArray.push(formattedData);
            }

            return (
                <ResponsiveContainer width={this.state.width} aspect={this.state.aspect}>
                    <AreaChart data={formattedDataArray}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="dateString" interval="preserveStartEnd"/>
                        <YAxis />
                        <Tooltip />
                        <Area type="monotone" dataKey="totalDistance" name="Insgesamt gesammelte Punkte" stroke={this.state.lineColor} fill={this.state.fillColor} />
                    </AreaChart>
                </ResponsiveContainer>

            );
        }
        else {
            return (
                <ResponsiveContainer width={this.state.width} aspect={this.state.aspect}>
                    <AreaChart data={data}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="dateString" interval="preserveStartEnd" />
                        <YAxis />
                        <Tooltip />
                        <Area type="monotone" dataKey="totalDistance" name="Insgesamt gesammelte Punkte" stroke={this.state.lineColor} fill={this.state.fillColor} />
                    </AreaChart>
                </ResponsiveContainer>
            );
        }
    }

    render() {
        return (
            <this.activitiesOverTimeChart />
        );
    }
}
export default ChallengeTeamPanelAreaGraph;