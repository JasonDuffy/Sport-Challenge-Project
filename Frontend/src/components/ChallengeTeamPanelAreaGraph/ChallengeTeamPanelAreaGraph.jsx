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
            width: props.width,
            aspect: props.aspect,
            lineColor: props.lineColor,
            fillColor: props.fillColor,
            activities: props.activities,
            startDate: props.startDate,
            endDate: props.endDate
        };

        this.activitiesOverTimeChart = this.activitiesOverTimeChart.bind(this);
    }

    /**
     * ASC Sorter for two object with dates
     * @param a Object with date compatible type on .date 
     * @param b Object with date compatible type on .date 
     * @returns Result of ASC sort
     */
    sortDatesASC(a, b) {
        return new Date(a.date) - new Date(b.date);
    }

    /**
     * Returns an area chart chronicling the progress of the given team over time
     */
    activitiesOverTimeChart() {
        let data = structuredClone(this.state.activities);

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
            // Variable for start of graph
            let start = 0;
            if (this.state.startDate === 0 || this.state.startDate === undefined)
                start = data.at(0).date.getTime() + (1000 * 3600 * 24); // Set to first data entry if startDate not defined
            else
                start = this.state.startDate.getTime();

            // Get current day for graph generation
            let end = new Date();
            if (this.state.endDate === 0 || this.state.endDate === undefined || this.state.endDate.getTime() > end.getTime())
                end = new Date(end.getFullYear(), end.getMonth(), end.getDate()).getTime() + (1000 * 3600 * 24); // Set to now if endDate not defined
            else
                end = this.state.endDate.getTime();
                

            // Add empty activities for every day from start of data to today
            // Fills empty spaces and makes x-axis scale even
            while (start <= end) {
                let newObj = {};
                newObj.date = new Date(start);
                newObj.date.setDate(newObj.date.getDate());
                newObj.dateString = newObj.date.toLocaleDateString('de-de', { day: "numeric", month: "long", year: "numeric" });
                newObj.totalDistance = 0;

                data.push(newObj);
                start += (1000 * 3600 * 24); // Adds one day in milliseconds
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
                        <XAxis dataKey="dateString" interval="preserveStartEnd" />
                        <YAxis />
                        <Tooltip />
                        <Area type="monotone" dataKey="totalDistance" name="Insgesamt gesammelte Punkte" stroke={this.state.lineColor} fill={this.state.fillColor} />
                    </AreaChart>
                </ResponsiveContainer>
            );
        }
        else {
            return;
        }
    }

    render() {
        return (
            <this.activitiesOverTimeChart />
        );
    }
}
export default ChallengeTeamPanelAreaGraph;