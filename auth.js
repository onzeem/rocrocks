// auth.js — shared, lightweight "signed in?" check for ROC Rocks Budgeting.
//
// This is a placeholder for real authentication. It uses localStorage so the
// demo works without a backend: signin.html / signup.html set the flag on
// submit, and settings.html can clear it on sign out. Swap isSignedIn() for
// a real session/token check whenever you wire up actual auth.

function isSignedIn() {
  return localStorage.getItem("rocSignedIn") === "true";
}

function setSignedIn(value) {
  if (value) {
    localStorage.setItem("rocSignedIn", "true");
  } else {
    localStorage.removeItem("rocSignedIn");
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const profileBtn = document.getElementById("profile-btn");
  const profileMenu = document.getElementById("profile-menu");
  const signinCta = document.getElementById("signin-cta");
  const signedIn = isSignedIn();

  if (profileBtn) {
    profileBtn.href = signedIn ? "profile.html" : "signin.html";
  }
  if (profileMenu) {
    profileMenu.classList.toggle("is-signed-in", signedIn);
    profileMenu.style.display = signedIn ? "flex" : "none";
  }
  if (signinCta) {
    signinCta.style.display = signedIn ? "none" : "inline-flex";
  }
});