let allCourses = [];

async function init() {
  await loadUsers();
  await loadAnalytics();
  await loadCourses();
  await loadRecommendations();
}

async function loadUsers() {
  try {
    const res = await fetch('/api/users');
    const users = await res.json();
    const select = document.getElementById('userSelect');
    select.innerHTML = users.map(u => `<option value="${u.id}">${u.name} (${u.role})</option>`).join('');
  } catch (e) { console.error('Error loading users:', e); }
}

async function loadAnalytics() {
  try {
    const res = await fetch('/api/analytics');
    const data = await res.json();
    document.getElementById('statUsers').innerText = data.totalUsers || 5;
    document.getElementById('statCourses').innerText = data.totalCourses || 10;
    document.getElementById('statEnrollments').innerText = data.totalEnrollments || 12;
  } catch (e) { console.error('Error loading analytics:', e); }
}

async function loadCourses() {
  try {
    const res = await fetch('/api/courses');
    allCourses = await res.json();
    renderCourses(allCourses, 'catalogGrid');
  } catch (e) { console.error('Error loading courses:', e); }
}

async function loadRecommendations() {
  const userId = document.getElementById('userSelect').value || 3;
  try {
    const res = await fetch(`/api/recommendations?userId=${userId}`);
    const recs = await res.json();
    renderCourses(recs, 'recommendationsGrid');
  } catch (e) { console.error('Error loading recs:', e); }
}

function renderCourses(courses, containerId) {
  const container = document.getElementById(containerId);
  if (!courses || courses.length === 0) {
    container.innerHTML = '<p class="subtext">No courses available.</p>';
    return;
  }
  container.innerHTML = courses.map(c => `
    <div class="course-card">
      <div>
        <span class="cat-badge">${c.category || 'GENERAL'}</span>
        <h4>${c.title}</h4>
        <p>${c.description || 'Comprehensive industry curriculum.'}</p>
      </div>
      <div class="card-footer">
        <span class="price">₹${c.price || 499}</span>
        <button class="btn-enroll" onclick="enroll(${c.id})">Enroll Now</button>
      </div>
    </div>
  `).join('');
}

function filterCourses() {
  const q = document.getElementById('searchInput').value.toLowerCase();
  const filtered = allCourses.filter(c => c.title.toLowerCase().includes(q) || (c.category && c.category.toLowerCase().includes(q)));
  renderCourses(filtered, 'catalogGrid');
}

async function enroll(courseId) {
  const userId = document.getElementById('userSelect').value || 3;
  try {
    const res = await fetch(`/api/enroll?userId=${userId}&courseId=${courseId}`, { method: 'POST' });
    const msg = await res.text();
    alert('Enrollment Status: ' + msg);
    loadAnalytics();
  } catch (e) { alert('Failed to enroll.'); }
}

window.onload = init;
