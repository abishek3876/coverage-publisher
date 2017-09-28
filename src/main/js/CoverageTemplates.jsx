import React from 'react';
import { JTable, TableHeaderRow } from '@jenkins-cd/design-language';

function basic() {
    const columns = [
        JTable.column(40, "Type"),
        JTable.column(60, "Values")
    ];
    return container(
        <JTable columns={columns}>
            <TableHeaderRow />
            {rows}
        </JTable>
    );
}
