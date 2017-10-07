import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { JTable, TableRow, TableCell, TableHeader } from '@jenkins-cd/design-language';
import { BarChart, Tooltip, Bar, XAxis, YAxis, ResponsiveContainer,
         LineChart, CartesianGrid, Legend, Line, Text
       } from 'recharts';

const color_good = "#78B037";
const color_bad = "#D54C53";
const color_warn = "#F5A623";
const color_info = "#4C9BD5";
const color_list = [
    "#4A90E2",
    "#D54C53",
    "#78B037",
    "#F5A623",
    "#BD0FE1",
];

class CoverageTrendGraph extends Component {
    /*
        coverageTrend = [{"Build: "1.0.123", "Branch": 23.6, ...}, ...]
    */
    current_color = 0;
    getColor() {
        var color = color_list[this.current_color];
        this.current_color += 1;
        if (this.current_color >= color_list.length) {
            this.current_color = 0;
        }
        return color;
    }

    render() {
        return (
            <div className="coverage">
                <h1>Code Coverage Trend</h1>
                <ResponsiveContainer width="100%" height={300}>
                    <LineChart data={this.props.coverageTrend}>
                        <XAxis dataKey="Build"/>
                        <YAxis domain={[0, 100]}/>
                        <CartesianGrid strokeDasharray="3 3"/>
                        <Tooltip formatter={value => (value + "%")} labelFormatter={label => ("Build: " + label)}/>
                        <Legend />
                        {
                            Object.keys(this.props.coverageTrend[0]).filter(coverageType => (coverageType !== "Build")).map(
                                coverageType => <Line dataKey={coverageType} stroke={this.getColor()}/>
                            )
                        }
                    </LineChart>
                </ResponsiveContainer>
            </div>
        );
    }
}

class CoverageSummaryTable extends Component {
    /*
        coverageSummary = {"Branch": {covered: 4, missed: 5}, ...}
    */
    render() {
        const columns = [
            JTable.column(15, "", false), // Coverage Type
            JTable.column(30, "", true) // Coverage Bar
        ];
        return (
            <div className="coverage">
                <h1>Code Coverage Summary</h1>
                <JTable columns={columns}>
                    {
                        Object.keys(this.props.coverageSummary).map( coverageType =>
                            <TableRow>
                                <TableCell>{coverageType}</TableCell>
                                <CoverageGraphCell coverageData={this.props.coverageSummary[coverageType]} />
                            </TableRow>
                        )
                    }
                </JTable>
            </div>
        );
    }
}

class CoverageGraphCell extends Component {
    /*
        coverageData = {covered: 4, missed: 5}
    */
    getTotal() {
        return this.props.coverageData.covered + this.props.coverageData.missed;
    }

    getPercent() {
        var total = this.getTotal();
        if (total == 0) {
            return "N/A";
        } else {
            return +(this.props.coverageData.covered/total * 100).toFixed(1) + "%";
        }
    }

    render() {
        return (
            <TableCell>
                <div style={{position: 'relative'}}>
                    <ResponsiveContainer width="100%" height={40}>
                        <BarChart layout="vertical" data={[this.props.coverageData]} stackOffset="expand">
                            <XAxis hide type="number"/>
                            <YAxis hide type="category"/>
                            <Tooltip cursor={false} labelFormatter={() => "Total : ".concat(this.getTotal())}/>
                            <Bar name="Covered" dataKey="covered" stackId="default" fill={color_good}/>
                            <Bar name="Missed" dataKey="missed" stackId="default" fill={color_bad}/>
                        </BarChart>
                    </ResponsiveContainer>
                    <p className="coverage-barchart-percent">{this.getPercent()}</p>
                </div>
            </TableCell>
        );
    }
}

ReactDOM.render(<CoverageTemplates />, document.getElementById("coverage"));