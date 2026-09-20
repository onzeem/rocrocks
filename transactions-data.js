// transactions-data.js — default seed data + shared helpers for transactions.
//
// `defaultTransactions` is only ever used to seed localStorage the very
// first time someone visits with an empty browser — after that, the saved
// array in localStorage (key "rocTransactions") is the real source of
// truth, and grows every time someone adds a transaction through the form
// on transactions.html.
//
// Each transaction is a plain object:
//   merchant   required  who the transaction was with
//   category   required  one of the keys in categoryMeta below
//   type       required  "in" or "out"
//   amount     required  a positive number, in dollars
//   date       required  "YYYY-MM-DD"

const defaultTransactions = [
  { merchant: "Green Leaf Market",     category: "Groceries",     type: "out", amount: 54.20,   date: "2026-09-19" },
  { merchant: "Payroll deposit",       category: "Income",        type: "in",  amount: 2950.00, date: "2026-09-18" },
  { merchant: "Riverside Apartments",  category: "Housing",       type: "out", amount: 1365.00, date: "2026-09-17" },
  { merchant: "Tanto Ramen",           category: "Dining out",    type: "out", amount: 28.75,   date: "2026-09-16" },
  { merchant: "Metro Transit Pass",    category: "Transport",     type: "out", amount: 81.00,   date: "2026-09-15" },
  { merchant: "Cloudline Storage",     category: "Subscriptions", type: "out", amount: 9.99,    date: "2026-09-14" },
  { merchant: "Basil & Vine",          category: "Dining out",    type: "out", amount: 41.10,   date: "2026-09-12" },
  { merchant: "Green Leaf Market",     category: "Groceries",     type: "out", amount: 67.85,   date: "2026-09-10" },
  { merchant: "City Water & Power",    category: "Utilities",     type: "out", amount: 96.40,   date: "2026-09-08" },
  { merchant: "SoundWave+",            category: "Subscriptions", type: "out", amount: 11.99,   date: "2026-09-06" },
  { merchant: "Metro Transit Pass",    category: "Transport",     type: "out", amount: 81.00,   date: "2026-09-05" },
  { merchant: "Green Leaf Market",     category: "Groceries",     type: "out", amount: 48.60,   date: "2026-09-03" },
  { merchant: "Riverside Apartments",  category: "Housing",       type: "out", amount: 1365.00, date: "2026-09-01" },
];

// Maps each category to the icon + color used elsewhere on the site, so a
// manually-added transaction looks consistent with the rest.
const categoryMeta = {
  "Groceries":     { icon: "🥑", color: "mint" },
  "Income":        { icon: "💰", color: "yellow" },
  "Housing":       { icon: "🏠", color: "grape" },
  "Dining out":    { icon: "🍜", color: "coral" },
  "Transport":     { icon: "🚌", color: "yellow" },
  "Subscriptions": { icon: "📺", color: "pink" },
  "Utilities":     { icon: "💧", color: "grape" },
  "Other":         { icon: "💳", color: "sky" },
};

function formatMoney(amount) {
  return `$${amount.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}

function formatDateLabel(dateStr) {
  return new Date(dateStr + "T00:00:00").toLocaleDateString("en-US", { month: "short", day: "numeric" });
}

// Shared renderer: builds one .txn-item for a transaction, matching the
// exact markup/classes used everywhere on the site.
function txnRowHtml(t) {
  const meta = categoryMeta[t.category] || categoryMeta["Other"];
  const isIn = t.type === "in";
  const amountHtml = isIn ? `+${formatMoney(t.amount)}` : `−${formatMoney(t.amount)}`;

  return `
    <li class="txn-item">
      <span class="cat-dot" style="background: var(--${meta.color}-tint)"><span class="cat-icon">${meta.icon}</span></span>
      <span>
        <span class="txn-merchant">${t.merchant}</span>
        <span class="txn-cat">${t.category} · ${formatDateLabel(t.date)}</span>
      </span>
      <span class="txn-amount tabular ${isIn ? "amt-in" : "amt-out"}">${amountHtml}</span>
    </li>
  `;
}

// Totals across a list of transactions — used to drive the stat row.
function computeTotals(list) {
  let moneyIn = 0;
  let moneyOut = 0;
  list.forEach((t) => {
    if (t.type === "in") {
      moneyIn += t.amount;
    } else {
      moneyOut += t.amount;
    }
  });
  return { moneyIn, moneyOut };
}