// Varsity Lend student portal: front-end prototype only. Not secure authentication.
const getProfile = () => {
  try { return JSON.parse(sessionStorage.getItem("vl_demo_profile") || "null"); }
  catch { return null; }
};
const register = document.getElementById("registerForm");
if (register) register.addEventListener("submit", e => {
  e.preventDefault();
  const data = Object.fromEntries(new FormData(register).entries());
  if (data.password.length < 8) return;
  sessionStorage.setItem("vl_demo_profile", JSON.stringify({
    name: data.name.trim(), email: data.email.trim(), phone: data.phone.trim(), institution: data.institution.trim()
  }));
  document.getElementById("registerMessage").textContent = "Demo account created. Opening your dashboard…";
  setTimeout(() => location.href = "dashboard.html", 700);
});
const login = document.getElementById("loginForm");
if (login) login.addEventListener("submit", e => {
  e.preventDefault();
  const profile = getProfile();
  const email = new FormData(login).get("email").trim();
  if (!profile || profile.email.toLowerCase() !== email.toLowerCase()) {
    document.getElementById("loginMessage").textContent = "No demo account found for that email. Create an account first.";
    return;
  }
  document.getElementById("loginMessage").textContent = "Demo login successful. Opening your dashboard…";
  setTimeout(() => location.href = "dashboard.html", 700);
});
const nameEl = document.getElementById("studentName");
if (nameEl) {
  const profile = getProfile();
  if (profile) {
    nameEl.textContent = profile.name.split(" ")[0];
    document.getElementById("studentEmail").textContent = profile.email;
    document.getElementById("studentInstitution").textContent = profile.institution;
  }
}
const logout = document.getElementById("logoutBtn");
if (logout) logout.addEventListener("click", () => sessionStorage.removeItem("vl_demo_profile"));
const apply = document.getElementById("applyForm");
if (apply) apply.addEventListener("submit", e => {
  e.preventDefault();
  document.getElementById("applyMessage").textContent = "Your application preview is ready. This prototype has not sent or saved your details.";
});