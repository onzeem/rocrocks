// categories-data.js — the single source of truth for every budget category.
//
// Both home.html (dashboard preview) and budget.html (pie chart + full bars)
// render from this same array. Add a new category, change a budgeted or
// spent amount, and it updates correctly everywhere — the pie chart's
// slices, its legend, and every bar all recompute automatically.
//
// Fields:
//   name    required  category name
//   icon    required  emoji shown in the colored dot
//   color   required  one of: grape, coral, mint, yellow, pink, sky
//                     (gray is reserved for the pie chart's "Other" slice —
//                     don't assign it to a real category)
//   cap     required  small caption under the name (e.g. "Rent, utilities")
//   spent   required  amount spent so far this month, in dollars
//   budget  required  amount budgeted for this category, in dollars
//
// To add a new category, just add another object below — no other file
// needs to change.

const categories = [
  { name: "Housing",       icon: "🏠", color: "grape",  cap: "Rent, utilities",       spent: 1365, budget: 1500 },
  { name: "Groceries",     icon: "🥑", color: "mint",   cap: "Food at home",          spent: 340,  budget: 500  },
  { name: "Transport",     icon: "🚌", color: "yellow", cap: "Fuel, transit",         spent: 141,  budget: 300  },
  { name: "Dining out",    icon: "🍜", color: "coral",  cap: "Restaurants, delivery", spent: 224,  budget: 200  },
  { name: "Subscriptions", icon: "📺", color: "pink",   cap: "Recurring services",    spent: 66,   budget: 80   },
  { name: "Entertainment", icon: "🎬", color: "sky",    cap: "Movies, streaming",     spent: 62,   budget: 90   },
];

// Shared renderer: builds one .category-row for a category, matching the
// exact markup/classes used everywhere on the site. Both home.html and
// budget.html call this instead of keeping their own copy of the markup.
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
      <div class="category-name">${c.name}<span class="cap">${c.cap}</span></div>
      <div class="bar-track"><div class="bar-fill${isOver ? " is-over" : ""}" style="--pct: ${barPct}; background: var(--${c.color});"></div></div>
      <div class="category-amount tabular">${amountHtml}</div>
      <div class="category-pct${isOver ? " is-over" : ""}">${isOver ? "⚠️ " : ""}${realPct}%</div>
    </div>
  `;
}

// Returns a new array with over-budget categories moved to the top, so the
// ones that need attention show up first — used instead of the raw
// `categories` array wherever the bars are rendered.
function categoriesSortedByOverBudget() {
  return [...categories].sort((a, b) => {
    const aOver = a.spent > a.budget ? 1 : 0;
    const bOver = b.spent > b.budget ? 1 : 0;
    return bOver - aOver;
  });
}