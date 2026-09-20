// goals-data.js — the single source of truth for every savings goal.
//
// Both home.html (compact sidebar cards) and savings.html (full grid)
// render from this same array, so a change here shows up correctly,
// automatically, on both pages — no more editing two copies by hand.
//
// Fields:
//   name      required  goal title
//   icon      required  emoji shown on the left of the header
//   af        required  "A" or "F" tag shown on the right of the header
//   saved     required  amount saved so far, in dollars
//   target    required  goal amount, in dollars
//   color     required  one of: grape, coral, mint, yellow, pink
//   dueDate   optional  shown as a small pill under the amount (savings.html)
//   badge     optional  shown in the card's bottom-right corner (savings.html)
//   note      optional  short status line (home.html sidebar cards)
//
// defaultGoals seeds localStorage the first time someone visits with an
// empty browser — after that, whatever's saved (key "rocGoals", grown by
// add-goal.html) is the real source of truth. Named "defaultGoals" rather
// than "goals" so it doesn't collide with the local `const goals =
// loadGoals();` each page declares for itself.

const defaultGoals = [
      {
        name: "Emergency fund",
        icon: "🛟",
        af: "A",
        saved: 4200,
        target: 6000,
        color: "grape",
      },
      {
        name: "Iceland trip",
        icon: "✈️",
        af: "F",
        saved: 980,
        target: 2500,
        color: "coral",
        dueDate: "Due: 08/01/2027",
        badge: "☕ 196 coffees saved",
      },
      {
        name: "New laptop",
        icon: "💻",
        af: "A",
        saved: 1100,
        target: 1100,
        color: "mint",
        badge: "☕ 84 coffees saved",
      },
    ];

const GOALS_STORAGE_KEY = "rocGoals";

function loadGoals() {
  try {
    const stored = localStorage.getItem(GOALS_STORAGE_KEY);
    return stored ? JSON.parse(stored) : [...defaultGoals];
  } catch {
    return [...defaultGoals];
  }
}

function saveGoals(goals) {
  localStorage.setItem(GOALS_STORAGE_KEY, JSON.stringify(goals));
}