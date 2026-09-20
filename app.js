// app.js - quick connection test
apiGet("/users")
  .then(users => {
    console.log("✅ Connected! Users:", users);
  })
  .catch(err => {
    console.error("❌ Connection failed:", err);
  });