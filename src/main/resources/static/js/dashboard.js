const csrfToken = document.querySelector('meta[name="csrf-token"]')?.content ?? "";
const alertBody = document.querySelector("#alerts-body");
const assetBody = document.querySelector("#assets-body");
const alertMessage = document.querySelector("#alert-message");

const severityOrder = ["CRITICAL", "HIGH", "MEDIUM", "LOW"];
const severityClass = (severity) => severity.toLowerCase();

async function apiGet(path) {
    const response = await fetch(path, { headers: { Accept: "application/json" } });
    if (!response.ok) {
        throw new Error(`Request failed (${response.status}). Refresh the page or sign in again.`);
    }
    return response.json();
}

function makeCell(text) {
    const cell = document.createElement("td");
    cell.textContent = text;
    return cell;
}

function renderSummary(summary) {
    document.querySelector("#metric-open").textContent = summary.openAlerts;
    document.querySelector("#metric-critical").textContent = summary.criticalAlerts;
    document.querySelector("#metric-assets").textContent = summary.monitoredAssets;
    document.querySelector("#metric-exposed").textContent = summary.exposedAssets;
    document.querySelector("#nav-open-count").textContent = summary.openAlerts;

    const score = Math.round(summary.averageRiskScore);
    document.querySelector("#risk-score").replaceChildren(document.createTextNode(String(score)));
    const suffix = document.createElement("span");
    suffix.textContent = "/100";
    document.querySelector("#risk-score").append(suffix);
    const riskLabel = score >= 70 ? "High risk" : score >= 40 ? "Moderate risk" : "Low risk";
    document.querySelector("#risk-label").textContent = riskLabel;
    document.querySelector("#risk-fill").style.width = `${Math.min(100, Math.max(0, score))}%`;

    const chart = document.querySelector("#severity-chart");
    const counts = summary.alertsBySeverity;
    const highest = Math.max(1, ...severityOrder.map((severity) => counts[severity] ?? 0));
    chart.replaceChildren(...severityOrder.map((severity) => {
        const row = document.createElement("div");
        row.className = "severity-row";
        const label = document.createElement("span");
        label.className = "severity-name";
        label.textContent = severity;
        const track = document.createElement("span");
        track.className = "severity-track";
        const fill = document.createElement("span");
        fill.className = `severity-fill ${severityClass(severity)}`;
        fill.style.width = `${((counts[severity] ?? 0) / highest) * 100}%`;
        track.append(fill);
        const count = document.createElement("span");
        count.className = "severity-count";
        count.textContent = counts[severity] ?? 0;
        row.append(label, track, count);
        return row;
    }));
}

function renderAlerts(alerts) {
    alertBody.replaceChildren();
    alertMessage.textContent = "";
    if (!alerts.length) {
        const row = document.createElement("tr");
        const cell = makeCell("No alerts match the selected filters.");
        cell.colSpan = 7;
        cell.className = "loading-cell";
        row.append(cell);
        alertBody.append(row);
    }

    for (const alert of alerts) {
        const row = document.createElement("tr");
        const severityCell = document.createElement("td");
        const severity = document.createElement("span");
        severity.className = `severity-badge ${severityClass(alert.severity)}`;
        severity.textContent = alert.severity;
        severityCell.append(severity);
        row.append(severityCell);

        const detailCell = document.createElement("td");
        const title = document.createElement("span");
        title.className = "alert-title";
        title.textContent = alert.title;
        const description = document.createElement("span");
        description.className = "alert-description";
        description.textContent = alert.description;
        detailCell.append(title, description);
        row.append(detailCell, makeCell(alert.assetId), makeCell(alert.source));

        const detected = new Date(alert.detectedAt);
        row.append(makeCell(Number.isNaN(detected.getTime()) ? "Unknown" : detected.toLocaleString()));

        const statusCell = document.createElement("td");
        const status = document.createElement("span");
        status.className = `status-badge ${severityClass(alert.status)}`;
        status.textContent = alert.status;
        statusCell.append(status);
        row.append(statusCell);

        const actionCell = document.createElement("td");
        if (alert.status === "OPEN") {
            const button = document.createElement("button");
            button.className = "ack-button";
            button.textContent = "Acknowledge";
            button.type = "button";
            button.setAttribute("aria-label", `Acknowledge ${alert.title}`);
            button.addEventListener("click", () => acknowledgeAlert(alert.id, button));
            actionCell.append(button);
        } else {
            actionCell.textContent = "—";
        }
        row.append(actionCell);
        alertBody.append(row);
    }

    document.querySelector("#alert-count").textContent = `${alerts.length} alert${alerts.length === 1 ? "" : "s"} shown`;
}

function renderAssets(assets) {
    assetBody.replaceChildren();
    for (const asset of assets) {
        const row = document.createElement("tr");
        const nameCell = document.createElement("td");
        const name = document.createElement("span");
        name.className = "asset-name";
        name.textContent = asset.name;
        const id = document.createElement("span");
        id.className = "asset-id";
        id.textContent = asset.id;
        nameCell.append(name, id);
        row.append(nameCell, makeCell(asset.type), makeCell(asset.environment));

        const scoreCell = document.createElement("td");
        scoreCell.className = "score-cell";
        scoreCell.append(document.createTextNode(String(asset.riskScore)));
        const track = document.createElement("span");
        track.className = "score-mini";
        const fill = document.createElement("span");
        fill.style.width = `${Math.min(100, Math.max(0, asset.riskScore))}%`;
        track.append(fill);
        scoreCell.append(track);
        row.append(scoreCell);

        const exposure = makeCell(asset.internetExposed ? "Public" : "Private");
        exposure.className = asset.internetExposed ? "exposure-public" : "exposure-private";
        row.append(exposure);
        const patch = makeCell(asset.patchStatus);
        const patchBadge = document.createElement("span");
        const patchClass = {
            Current: "current",
            "Needs review": "needs-review",
            "Pending patch": "pending-patch"
        }[asset.patchStatus] ?? "needs-review";
        patchBadge.className = `patch-badge ${patchClass}`;
        patchBadge.textContent = asset.patchStatus;
        patch.append(patchBadge);
        row.append(patch, makeCell(asset.owner));
        assetBody.append(row);
    }
}

async function loadAlerts() {
    const query = new URLSearchParams();
    const severity = document.querySelector("#severity-filter").value;
    const status = document.querySelector("#status-filter").value;
    const search = document.querySelector("#alert-search").value.trim();
    if (severity) query.set("severity", severity);
    if (status) query.set("status", status);
    if (search) query.set("search", search);
    try {
        renderAlerts(await apiGet(`/api/alerts?${query.toString()}`));
    } catch (error) {
        alertMessage.textContent = error.message;
    }
}

async function acknowledgeAlert(id, button) {
    button.disabled = true;
    alertMessage.textContent = "";
    try {
        const response = await fetch(`/api/alerts/${encodeURIComponent(id)}/acknowledge`, {
            method: "POST",
            headers: { Accept: "application/json", "X-XSRF-TOKEN": csrfToken }
        });
        if (!response.ok) {
            const message = response.status === 409
                ? "This alert has already changed status. The list has been refreshed."
                : `Unable to acknowledge alert (${response.status}). Refresh or sign in again.`;
            throw new Error(message);
        }
        await Promise.all([loadAlerts(), loadSummary()]);
    } catch (error) {
        alertMessage.textContent = error.message;
        button.disabled = false;
    }
}

async function loadSummary() {
    renderSummary(await apiGet("/api/dashboard"));
}

async function loadDashboard() {
    try {
        await Promise.all([
            loadSummary(),
            loadAlerts(),
            apiGet("/api/assets").then(renderAssets)
        ]);
    } catch (error) {
        alertMessage.textContent = error.message;
    }
}

document.querySelector("#current-date").textContent = new Intl.DateTimeFormat(undefined, {
    weekday: "short",
    month: "short",
    day: "numeric",
    year: "numeric"
}).format(new Date());
document.querySelector("#severity-filter").addEventListener("change", loadAlerts);
document.querySelector("#status-filter").addEventListener("change", loadAlerts);
let searchTimeout;
document.querySelector("#alert-search").addEventListener("input", () => {
    window.clearTimeout(searchTimeout);
    searchTimeout = window.setTimeout(loadAlerts, 180);
});
loadDashboard();
