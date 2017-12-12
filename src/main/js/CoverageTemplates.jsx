import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { JTable } from './JTable';
import { TableRow, TableHeaderRow } from './TableRow';
import { TableCell } from './TableCell';
import { BarChart, Tooltip, Bar, XAxis, YAxis, ResponsiveContainer,
         LineChart, CartesianGrid, Legend, Line
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
    "#24B0D5",
    "#949393",
    "#8CC04F",
    "#F6B44B",
];

class CoverageTrendGraph extends Component {
    /*
        coverageTrend = [{"Build: "1.0.123", "Branch": 23.6, ...}, ...]
    */
    render() {
        const strokeColors = color_list.slice();
        return (
            <div className="coverage">
                <h3>Code Coverage Trend</h3>
                <ResponsiveContainer width="100%" height={300}>
                    <LineChart data={this.props.coverageTrend}>
                        <XAxis dataKey="Build"/>
                        <YAxis domain={[0, 100]}/>
                        <CartesianGrid strokeDasharray="3 3"/>
                        <Tooltip formatter={value => (value + "%")} labelFormatter={label => ("Build: " + label)}/>
                        <Legend />
                        {
                            Object.keys(this.props.coverageTrend[0]).filter(coverageType => (coverageType !== "Build")).map(
                                coverageType => <Line dataKey={coverageType} stroke={strokeColors.shift() || "#4A4A4A"}/>
                            )
                        }
                    </LineChart>
                </ResponsiveContainer>
            </div>
        );
    }
}

class CoverageSummary extends Component {
    /*
        coverageSummary = {"Branch": {covered: 4, missed: 5}, ...}
    */
    render() {
        return (
            <div className="coverage">
                <h3>Code Coverage Summary</h3>
                <CoverageSummaryTable coverageSummary={this.props.coverageSummary}/>
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
            JTable.column(50, "", false), // Coverage Type
            JTable.column(30, "", true) // Coverage Bar
        ];
        return (
            <JTable className="coverage" columns={columns}>
                {
                    Object.keys(this.props.coverageSummary).map( coverageType =>
                        <TableRow>
                            <TableCell>{coverageType}</TableCell>
                            <CoverageGraphCell coverageData={this.props.coverageSummary[coverageType]} />
                        </TableRow>
                    )
                }
            </JTable>
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

class CoverageData extends Component {
    normalizeName(name) {
        return name.toLowerCase().replace(/\W+/g, ".");
    }

    /*
        coverageData = {"name": "Report", "coverageSummary": {}, "children": {}...}
    */
    renderDataTable() {
        if (this.props.coverageData.children.data.length > 0) {
            const columns = Object.keys(this.props.coverageData.children.data[0].coverageSummary);
            var tableColumns = [
                JTable.column(250, "NAME", false)
            ];
            columns.forEach( column =>
                tableColumns.push(JTable.column(100, column, true))
            );
            const isRefed = this.props.coverageData.children.isRefed;

            if (isRefed) {
                return (
                    <div className="coverage">
                        <h3>{this.props.coverageData.children.type}</h3>
                        <JTable columns={tableColumns}>
                            <TableHeaderRow/>
                            {
                                this.props.coverageData.children.data.map( child =>
                                    <TableRow href={this.normalizeName(child.name)}>
                                        <TableCell>{child.name}</TableCell>
                                        {
                                            columns.map( column =>
                                                <CoverageGraphCell coverageData={child.coverageSummary[column]} />
                                            )
                                        }
                                    </TableRow>
                                )
                            }
                        </JTable>
                    </div>
                );
            } else {
                return (
                    <div className="coverage">
                        <h3>{this.props.coverageData.children.type}</h3>
                        <JTable columns={tableColumns}>
                            <TableHeaderRow/>
                            {
                                this.props.coverageData.children.data.map( child =>
                                    <TableRow>
                                        <TableCell>{child.name}</TableCell>
                                        {
                                            columns.map( column =>
                                                <CoverageGraphCell coverageData={child.coverageSummary[column]} />
                                            )
                                        }
                                    </TableRow>
                                )
                            }
                        </JTable>
                    </div>
                );
            }
        }
    }

    getBackgroundColor(line) {
        switch(line[1]) {
            case 0:
                return color_bad;
            case 1:
                return color_good;
            case 2:
                return color_warn;
            default:
                return "";
        }
    }

    getBranchCounters(line) {
        if (line[2] != 0 || line[3] != 0) {
            return (
                <td><span style={{color: color_good}}>{line[2]}</span>/<span style={{color: color_bad}}>{line[3]}</span></td>
            );
        }
    }

    renderSourceFile() {
        if (this.props.coverageData.sourceFile) {
            var lineNo = 1;
            return (
                <table className="coverage-code">
                    <tbody>
                    {
                        this.props.coverageData.sourceFile.map( line =>
                            <tr>
                                <td style={{color: "rgba(255, 255, 255, 0.5)"}}>{lineNo++}</td>
                                <td style={{backgroundColor: this.getBackgroundColor(line)}}>{line[0]}</td>
                                {this.getBranchCounters(line)}
                            </tr>
                        )
                    }
                    </tbody>
                </table>
            );
        }
    }

    render() {
        var name = "";
        this.props.coverageData.path.filter(node => (node.length > 0)).forEach( node =>
            name = node + " >> "
        );
        name = name + this.props.coverageData.name;
        if (name.length == 0) {
            name = "Code Coverage Report";
        }
        return (
            <div>
                <h2 className="coverage">{name}</h2>
                <CoverageSummaryTable coverageSummary={this.props.coverageData.coverageSummary}/>
                {this.renderDataTable()}
                {this.renderSourceFile()}
            </div>
        );
    }
}

global.renderCoverageSummary = function() {
    Q.getJSON("coverage/api/json", function (data) {
        ReactDOM.render(<CoverageSummary coverageSummary={data.coverageData.coverageSummary} />, document.getElementById("coverage"));
    });
}

global.renderCoverageTrend = function(isProjectPage) {
    var url = "api/json";
    url = isProjectPage? "coverage/" + url : url;
    Q.getJSON(url, function (data) {
        ReactDOM.render(<CoverageTrendGraph coverageTrend={data.coverageTrend} />, document.getElementById("coverage"));
    });
}

global.renderCoverageData = function() {
    Q.getJSON("api/json", function (data) {
        ReactDOM.render(<CoverageData coverageData={data.coverageData} />, document.getElementById("coverage"));
    });
}
