// categories-data.js — shared rendering helper for budget category rows.
//
// Category display metadata (icon, color, name, description) now lives
// in the backend's `categories` table (see Category.java / data.sql),
// not here — the API sends icon/color back directly on every category
// in a budget-vs-actual report, so home.html and budget.html read
// rc.icon / rc.color straight off the response instead of looking a
// name up in a separate local table. This file is just the one shared
// HTML template both pages use to draw a row once they already have
// the real numbers and icon/color in hand.

// Shared renderer: builds one .category-row for a category, matching the
// exact markup/classes used everywhere on the site. Both home.html and
// budget.html call this instead of keeping their own copy of the markup.
//
// Expects: { name, icon, color, cap, spent, budget }
function categoryRowHtml(c) {
  const isOver = c.spent > c.budget;
  const realPct = Math.round((c.spent / c.budget) * 100); // uncapped, for the number shown
  // The bar's own width: normal percentage of the track when under budget.
  // When over, spill a small FIXED pixel amount past the track's right
  // edge instead of a percentage — a percentage-based overflow looks fine
  // on a narrow mobile track, but on a wide desktop track (which can be
  // several hundred px) the same percentage becomes a large number of
  // pixels and crowds into the amount column next to it.
  const barPct = isOver ? "calc(100% + 50px)" : `${realPct}%`;
  const amountHtml = isOver
    ? `$${c.spent.toLocaleString()}<span class="of is-over">$${(c.spent - c.budget).toLocaleString()} over</span>`
    : `$${c.spent.toLocaleString()}<span class="of">of $${c.budget.toLocaleString()}</span>`;

  return `
    <div class="category-row${isOver ? " is-over-budget" : ""}">
      <div class="cat-dot" style="background: var(--${c.color}-tint)"><span class="cat-icon">${c.icon}</span></div>
      <div class="category-name">${c.name}<span class="cap">${c.cap || ""}</span></div>
      <div class="bar-track"><div class="bar-fill${isOver ? " is-over" : ""}" style="--pct: ${barPct}; background: var(--${c.color});"></div></div>
      <div class="category-amount tabular">${amountHtml}</div>
      <div class="category-pct${isOver ? " is-over" : ""}">${isOver ? "⚠️ " : ""}${realPct}%</div>
    </div>
  `;
}