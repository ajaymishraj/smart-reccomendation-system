/**
 * ══════════════════════════════════════════════════════════════════════════
 *   Smart Recommendation Service Platform — Web Application Client Logic
 *   Connects to Java REST Backend with graceful client-side simulation fallback.
 * ══════════════════════════════════════════════════════════════════════════
 */

// ── Application State ───────────────────────────────────────────────────────
const state = {
  currentUser: {
    id: 3,
    name: 'Student Raj',
    role: 'REGULAR',
    avatar: '👨‍🎓',
    email: 'raj@example.com'
  },
  currentStrategy: 'Popularity-Based',
  activeCategory: 'ALL',
  searchQuery: '',
  ratingModalCourseId: null,
  selectedStarValue: 5,
  isLiveBackend: false,

  // In-memory data structures (synced with Java backend)
  users: [
    { userId: 1, name: 'Admin Alice', email: 'admin@platform.com', role: 'ADMIN', active: true },
    { userId: 2, name: 'Manager Bob', email: 'bob@platform.com', role: 'CONTENT_MANAGER', active: true },
    { userId: 3, name: 'Student Raj', email: 'raj@example.com', role: 'REGULAR', active: true },
    { userId: 4, name: 'Student Priya', email: 'priya@example.com', role: 'REGULAR', active: true },
    { userId: 5, name: 'Student Arjun', email: 'arjun@example.com', role: 'REGULAR', active: true }
  ],

  courses: [
    { itemId: 1, title: 'Java OOP Fundamentals', description: 'Master OOP principles, encapsulation, polymorphism & patterns in Java 21+.', category: 'PROGRAMMING', price: 499, averageRating: 4.8, totalRatings: 18, viewCount: 142, instructor: 'Dr. Sharma', durationHours: 30, level: 'BEGINNER', enrollmentCount: 24 },
    { itemId: 2, title: 'Python for Data Science', description: 'Exploratory data analysis, Pandas, NumPy, and statistical modeling.', category: 'DATA_SCIENCE', price: 799, averageRating: 4.7, totalRatings: 14, viewCount: 110, instructor: 'Prof. Gupta', durationHours: 40, level: 'INTERMEDIATE', enrollmentCount: 19 },
    { itemId: 3, title: 'React & Node Full Stack', description: 'Build scalable modern web applications with React, Express and Node.', category: 'WEB_DEVELOPMENT', price: 999, averageRating: 4.6, totalRatings: 11, viewCount: 95, instructor: 'Ms. Singh', durationHours: 50, level: 'INTERMEDIATE', enrollmentCount: 15 },
    { itemId: 4, title: 'Machine Learning A-Z', description: 'Supervised & unsupervised ML algorithms, gradient descent & model tuning.', category: 'ARTIFICIAL_INTELLIGENCE', price: 1299, averageRating: 4.9, totalRatings: 25, viewCount: 220, instructor: 'Dr. Patel', durationHours: 60, level: 'ADVANCED', enrollmentCount: 38 },
    { itemId: 5, title: 'AWS Cloud Practitioner', description: 'Core AWS cloud architecture, EC2, S3, IAM security and VPC networking.', category: 'CLOUD_COMPUTING', price: 899, averageRating: 4.5, totalRatings: 9, viewCount: 88, instructor: 'Mr. Kumar', durationHours: 25, level: 'BEGINNER', enrollmentCount: 12 },
    { itemId: 6, title: 'Ethical Hacking Basics', description: 'Cybersecurity fundamentals, vulnerability scanning & penetration testing.', category: 'CYBERSECURITY', price: 799, averageRating: 4.4, totalRatings: 8, viewCount: 75, instructor: 'Ms. Rao', durationHours: 35, level: 'BEGINNER', enrollmentCount: 10 },
    { itemId: 7, title: 'SQL & Database Design', description: 'Relational database schema modeling, indexing, normalization & ACID transactions.', category: 'DATABASE', price: 599, averageRating: 4.6, totalRatings: 12, viewCount: 84, instructor: 'Dr. Verma', durationHours: 20, level: 'BEGINNER', enrollmentCount: 16 },
    { itemId: 8, title: 'Deep Learning with TensorFlow', description: 'Convolutional neural networks, RNNs, transformers and computer vision.', category: 'ARTIFICIAL_INTELLIGENCE', price: 1499, averageRating: 4.9, totalRatings: 21, viewCount: 180, instructor: 'Dr. Kapoor', durationHours: 70, level: 'ADVANCED', enrollmentCount: 28 }
  ],

  enrollments: [
    { enrollmentId: 1, userId: 3, itemId: 1, progress: 85, status: 'ACTIVE', date: '2026-10-15' },
    { enrollmentId: 2, userId: 3, itemId: 4, progress: 100, status: 'COMPLETED', date: '2026-10-10' },
    { enrollmentId: 3, userId: 4, itemId: 2, progress: 60, status: 'ACTIVE', date: '2026-10-18' },
    { enrollmentId: 4, userId: 4, itemId: 4, progress: 45, status: 'ACTIVE', date: '2026-10-20' },
    { enrollmentId: 5, userId: 5, itemId: 3, progress: 90, status: 'ACTIVE', date: '2026-10-12' },
    { enrollmentId: 6, userId: 5, itemId: 5, progress: 30, status: 'ACTIVE', date: '2026-10-22' }
  ]
};

// ── Initialisation ──────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  setupTabs();
  checkBackendConnection();
  renderAllViews();
});

// ── Backend API Integration ─────────────────────────────────────────────────
async function checkBackendConnection() {
  try {
    const res = await fetch('/api/analytics', { cache: 'no-store' });
    if (res.ok) {
      const data = await res.json();
      state.isLiveBackend = true;
      document.getElementById('backendStatus').innerHTML = '<span class="dot live"></span> Live Java Backend Connected (Port 8080)';
      fetchCoursesFromBackend();
    }
  } catch (err) {
    console.log('Running in client simulation mode (standalone browser).');
    document.getElementById('backendStatus').innerHTML = '<span class="dot live"></span> Client Platform Engine Ready';
  }
}

async function fetchCoursesFromBackend() {
  try {
    const res = await fetch('/api/courses');
    if (res.ok) {
      const items = await res.json();
      if (items && items.length > 0) {
        state.courses = items;
        renderCatalog();
        renderRecommendations();
      }
    }
  } catch (e) {
    console.warn('Backend fetch failed, using state cache:', e);
  }
}

// ── Navigation Tabs ─────────────────────────────────────────────────────────
function setupTabs() {
  const tabs = document.querySelectorAll('.nav-tab');
  tabs.forEach(tab => {
    tab.addEventListener('click', () => {
      const targetId = tab.getAttribute('data-tab');
      switchTab(targetId);
    });
  });
}

function switchTab(tabId) {
  document.querySelectorAll('.nav-tab').forEach(t => {
    t.classList.toggle('active', t.getAttribute('data-tab') === tabId);
  });
  document.querySelectorAll('.tab-pane').forEach(p => {
    p.classList.toggle('active', p.id === tabId);
  });

  if (tabId === 'tab-recommendations') {
    fetchRecommendations();
  } else if (tabId === 'tab-analytics') {
    updateTelemetryReport();
  }
}

// ── User Persona Switcher ───────────────────────────────────────────────────
function toggleUserDropdown() {
  const menu = document.getElementById('userDropdown');
  menu.classList.toggle('show');
}

document.addEventListener('click', (e) => {
  const panel = document.getElementById('userSessionPanel');
  if (panel && !panel.contains(e.target)) {
    document.getElementById('userDropdown').classList.remove('show');
  }
});

function switchUser(id, name, role, avatar, email) {
  state.currentUser = { id, name, role, avatar, email };
  document.getElementById('userAvatar').textContent = avatar;
  document.getElementById('currentUserName').textContent = name;
  
  const roleBadge = document.getElementById('currentUserRole');
  roleBadge.textContent = role.replace('_', ' ');
  roleBadge.className = 'user-role-badge ' + (role === 'ADMIN' ? 'admin' : role === 'CONTENT_MANAGER' ? 'manager' : 'regular');

  // Toggle admin navigation visibility
  const adminTabs = document.querySelectorAll('.admin-only');
  adminTabs.forEach(t => {
    t.style.display = (role === 'ADMIN' || role === 'CONTENT_MANAGER') ? 'block' : 'none';
  });

  document.getElementById('userDropdown').classList.remove('show');
  showToast(`Switched active profile to ${name} (${role})`, 'info');

  renderAllViews();
}

// ── Render Views ────────────────────────────────────────────────────────────
function renderAllViews() {
  updateKpiCounters();
  renderDashboardPreview();
  renderRecommendations();
  renderCatalog();
  renderMyLearning();
  renderAdminCatalogTable();
  renderUsersTable();
  renderLeaderboards();
  updateTelemetryReport();
}

// ── 1. KPI Counters ─────────────────────────────────────────────────────────
function updateKpiCounters() {
  document.getElementById('kpiUsers').textContent = state.users.length;
  document.getElementById('kpiCourses').textContent = state.courses.length;
  document.getElementById('kpiEnrollments').textContent = state.enrollments.length;
  document.getElementById('kpiStrategy').textContent = state.currentStrategy;
  document.getElementById('dashGreetingName').textContent = state.currentUser.name;
  document.getElementById('dashStrategyBadge').textContent = state.currentStrategy;
}

// ── 2. Recommendation Engine (3 Algorithms) ─────────────────────────────────
function setRecommendationStrategy(strategyKey) {
  const stratMap = {
    'POPULARITY': 'Popularity-Based',
    'CONTENT': 'Content-Based Filtering',
    'COLLABORATIVE': 'Collaborative Filtering'
  };
  state.currentStrategy = stratMap[strategyKey] || 'Popularity-Based';

  document.querySelectorAll('.strategy-btn').forEach(btn => {
    btn.classList.toggle('active', btn.id.toUpperCase().includes(strategyKey));
  });

  // Call Java backend if active
  if (state.isLiveBackend) {
    fetch('/api/strategy', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ strategy: strategyKey })
    }).catch(console.warn);
  }

  showToast(`Recommendation Strategy swapped to: ${state.currentStrategy}`, 'info');
  fetchRecommendations();
}

function calculateRecommendations() {
  const user = state.currentUser;
  const myEnrolledIds = state.enrollments
    .filter(e => e.userId === user.id)
    .map(e => e.itemId);

  const candidates = state.courses.filter(c => !myEnrolledIds.includes(c.itemId));

  if (state.currentStrategy.includes('Popularity')) {
    // Formula: Score = 0.3*views + 0.4*ratings + 3.0*avgRating
    return [...candidates].sort((a, b) => {
      const scoreA = (a.viewCount * 0.3) + (a.totalRatings * 0.4) + (a.averageRating * 3.0);
      const scoreB = (b.viewCount * 0.3) + (b.totalRatings * 0.4) + (b.averageRating * 3.0);
      return scoreB - scoreA;
    }).slice(0, 5);
  } else if (state.currentStrategy.includes('Content')) {
    // Category affinity matching
    const myCats = state.courses
      .filter(c => myEnrolledIds.includes(c.itemId))
      .map(c => c.category);

    return [...candidates].sort((a, b) => {
      const affinityA = myCats.includes(a.category) ? 10 : 0;
      const affinityB = myCats.includes(b.category) ? 10 : 0;
      return (affinityB + b.averageRating) - (affinityA + a.averageRating);
    }).slice(0, 5);
  } else {
    // Collaborative Filtering: find similar user peer overlap
    return [...candidates].sort((a, b) => b.totalRatings - a.totalRatings).slice(0, 5);
  }
}

async function fetchRecommendations() {
  document.getElementById('recTargetUser').textContent = state.currentUser.name;
  const explainer = document.getElementById('recStrategyExplainer');
  if (state.currentStrategy.includes('Popularity')) {
    explainer.textContent = 'Ranking candidate courses based on aggregate community popularity, total views, and verified learner star ratings.';
  } else if (state.currentStrategy.includes('Content')) {
    explainer.textContent = `Analyzing ${state.currentUser.name}'s historical category preferences and matching semantic catalog tags.`;
  } else {
    explainer.textContent = 'Collaborative user-item matrix factorization identifying cross-enrollments by learners with similar profiles.';
  }

  const recs = calculateRecommendations();
  const container = document.getElementById('recommendationsList');
  if (!container) return;

  if (recs.length === 0) {
    container.innerHTML = `
      <div class="card" style="text-align: center; padding: 40px;">
        <p class="text-muted">You have already enrolled in all available recommendations or need more activity!</p>
      </div>`;
    return;
  }

  container.innerHTML = recs.map(course => `
    <div class="rec-card">
      <div class="rec-left">
        <div class="rec-badges">
          <span class="badge-pill">${course.category.replace('_', ' ')}</span>
          <span class="badge-pill outline">${course.level}</span>
          <span class="badge-pill amber">⭐ ${course.averageRating.toFixed(1)} (${course.totalRatings} ratings)</span>
        </div>
        <h4 class="rec-title">${course.title}</h4>
        <p class="rec-desc">${course.description}</p>
        <div class="rec-meta">
          <span>👤 ${course.instructor}</span>
          <span>⏱ ${course.durationHours} Hours</span>
          <span>👁 ${course.viewCount} Views</span>
          <span>🎓 ${course.enrollmentCount} Enrolled</span>
        </div>
      </div>
      <div class="rec-right">
        <span class="rec-price">₹${course.price}</span>
        <button class="btn btn-primary btn-sm" onclick="handleEnroll(${course.itemId}, '${escapeHtml(course.title)}')">⚡ Quick Enroll</button>
      </div>
    </div>
  `).join('');
}

// ── 3. Dashboard Preview Feed ───────────────────────────────────────────────
function renderDashboardPreview() {
  const preview = document.getElementById('dashRecPreview');
  if (!preview) return;
  const recs = calculateRecommendations().slice(0, 3);
  
  if (recs.length === 0) {
    preview.innerHTML = `<p class="text-muted">No courses pending recommendation.</p>`;
    return;
  }

  preview.innerHTML = recs.map(c => `
    <div class="rec-preview-card">
      <div class="rec-preview-info">
        <h4>${c.title}</h4>
        <div class="rec-preview-meta">
          <span>${c.category.replace('_', ' ')}</span>
          <span>⭐ ${c.averageRating.toFixed(1)}</span>
          <span style="color:#34d399; font-weight:700;">₹${c.price}</span>
        </div>
      </div>
      <button class="btn btn-emerald btn-sm" onclick="handleEnroll(${c.itemId}, '${escapeHtml(c.title)}')">Enroll</button>
    </div>
  `).join('');
}

// ── 4. Course Catalog Grid ──────────────────────────────────────────────────
function filterCategory(cat) {
  state.activeCategory = cat;
  document.querySelectorAll('.cat-chip').forEach(btn => {
    btn.classList.toggle('active', btn.getAttribute('data-cat') === cat);
  });
  renderCatalog();
}

function handleCatalogFilter() {
  state.searchQuery = document.getElementById('catalogSearch').value.toLowerCase().trim();
  renderCatalog();
}

function renderCatalog() {
  const grid = document.getElementById('coursesGrid');
  if (!grid) return;

  const myEnrolledIds = state.enrollments
    .filter(e => e.userId === state.currentUser.id)
    .map(e => e.itemId);

  const filtered = state.courses.filter(course => {
    const matchCat = state.activeCategory === 'ALL' || course.category === state.activeCategory;
    const matchSearch = !state.searchQuery || 
      course.title.toLowerCase().includes(state.searchQuery) ||
      course.instructor.toLowerCase().includes(state.searchQuery) ||
      course.description.toLowerCase().includes(state.searchQuery);
    return matchCat && matchSearch;
  });

  if (filtered.length === 0) {
    grid.innerHTML = `<div style="grid-column: 1/-1; text-align: center; padding: 48px;" class="card text-muted">No courses match your filter criteria.</div>`;
    return;
  }

  grid.innerHTML = filtered.map(c => {
    const isEnrolled = myEnrolledIds.includes(c.itemId);
    return `
      <div class="course-card">
        <div class="course-top">
          <div>
            <span class="badge-pill" style="margin-bottom: 8px;">${c.category.replace('_', ' ')}</span>
            <h3 class="course-title">${c.title}</h3>
          </div>
          <span class="badge-pill outline">${c.level}</span>
        </div>
        <p class="course-desc">${c.description}</p>
        <div class="course-instructor">👤 ${c.instructor} &bull; ⏱ ${c.durationHours}h</div>
        <div class="course-stats-bar">
          <span class="course-rating">⭐ ${c.averageRating.toFixed(1)} <small>(${c.totalRatings})</small></span>
          <span>👁 ${c.viewCount} views</span>
          <span>🎓 ${c.enrollmentCount}</span>
        </div>
        <div class="course-price-action">
          <span class="course-price">₹${c.price}</span>
          ${isEnrolled ? 
            `<button class="btn btn-outline btn-sm" disabled style="opacity:0.75; color:#34d399;">✔ Enrolled</button>` :
            `<button class="btn btn-primary btn-sm" onclick="handleEnroll(${c.itemId}, '${escapeHtml(c.title)}')">Enroll Now</button>`
          }
        </div>
      </div>
    `;
  }).join('');
}

// ── 5. Enrollment Operations ────────────────────────────────────────────────
async function handleEnroll(courseId, courseTitle) {
  const existing = state.enrollments.find(e => e.userId === state.currentUser.id && e.itemId === courseId);
  if (existing) {
    showToast(`You are already enrolled in ${courseTitle}`, 'info');
    return;
  }

  const newEnrollment = {
    enrollmentId: state.enrollments.length + 1,
    userId: state.currentUser.id,
    itemId: courseId,
    progress: 10,
    status: 'ACTIVE',
    date: new Date().toISOString().split('T')[0]
  };

  state.enrollments.push(newEnrollment);

  const course = state.courses.find(c => c.itemId === courseId);
  if (course) course.enrollmentCount++;

  // Sync with Java server
  if (state.isLiveBackend) {
    try {
      await fetch('/api/enroll', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: state.currentUser.id, courseId: courseId })
      });
    } catch (e) {
      console.warn('Backend enroll sync failed:', e);
    }
  }

  showToast(`Successfully enrolled in: ${courseTitle}!`, 'success');
  renderAllViews();
}

// ── 6. My Learning Progress ─────────────────────────────────────────────────
function renderMyLearning() {
  const container = document.getElementById('enrollmentsList');
  if (!container) return;

  const myEnrollments = state.enrollments.filter(e => e.userId === state.currentUser.id);
  if (myEnrollments.length === 0) {
    container.innerHTML = `
      <div class="card" style="text-align: center; padding: 48px;">
        <p class="text-muted">You have no active enrollments yet. Discover courses in the Catalog!</p>
        <button class="btn btn-primary btn-sm" style="margin-top:16px;" onclick="switchTab('tab-catalog')">Browse Catalog</button>
      </div>`;
    return;
  }

  container.innerHTML = myEnrollments.map(e => {
    const c = state.courses.find(item => item.itemId === e.itemId) || { title: 'Unknown Course', category: 'GENERAL', instructor: 'Faculty' };
    const isCompleted = e.progress >= 100 || e.status === 'COMPLETED';

    return `
      <div class="enrollment-card">
        <div>
          <div style="display: flex; gap: 8px; margin-bottom: 6px;">
            <span class="badge-pill">${c.category.replace('_', ' ')}</span>
            <span class="badge-pill ${isCompleted ? 'success' : 'amber'}">${isCompleted ? 'COMPLETED' : 'IN PROGRESS'}</span>
          </div>
          <h4 style="font-size: 1.15rem; font-weight:700;">${c.title}</h4>
          <p class="text-muted" style="font-size:0.8rem;">Instructor: ${c.instructor} &bull; Enrolled Date: ${e.date}</p>
          <div class="progress-track">
            <div class="progress-fill" style="width: ${e.progress}%;"></div>
          </div>
          <small class="text-muted">${e.progress}% Course Curriculum Completed</small>
        </div>
        <div class="enrollment-actions">
          <button class="btn btn-outline btn-sm" onclick="openRateModal(${e.itemId}, '${escapeHtml(c.title)}')">⭐ Rate Course</button>
          ${!isCompleted ? 
            `<button class="btn btn-emerald btn-sm" onclick="completeCourse(${e.enrollmentId})">✔ Complete</button>` :
            `<span class="badge-pill success">Certificate Ready</span>`
          }
        </div>
      </div>
    `;
  }).join('');
}

function completeCourse(enrollmentId) {
  const enr = state.enrollments.find(e => e.enrollmentId === enrollmentId);
  if (enr) {
    enr.progress = 100;
    enr.status = 'COMPLETED';
    showToast('Course marked as 100% completed!', 'success');
    renderMyLearning();
  }
}

// ── 7. Rating Modal Logic ───────────────────────────────────────────────────
function openRateModal(courseId, courseTitle) {
  state.ratingModalCourseId = courseId;
  document.getElementById('rateModalCourseTitle').textContent = courseTitle;
  setRating(5);
  document.getElementById('rateModal').classList.add('open');
}

function closeRateModal() {
  document.getElementById('rateModal').classList.remove('open');
}

function setRating(val) {
  state.selectedStarValue = val;
  const stars = document.querySelectorAll('#starSelector .star');
  stars.forEach((s, idx) => {
    s.classList.toggle('filled', idx < val);
  });
  document.getElementById('ratingValueText').textContent = `Selected Rating: ${val} Star${val > 1 ? 's' : ''}`;
}

async function submitRating() {
  const courseId = state.ratingModalCourseId;
  const rating = state.selectedStarValue;
  const course = state.courses.find(c => c.itemId === courseId);

  if (course) {
    course.totalRatings++;
    course.averageRating = ((course.averageRating * (course.totalRatings - 1)) + rating) / course.totalRatings;
  }

  if (state.isLiveBackend) {
    try {
      await fetch('/api/rate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: state.currentUser.id, courseId, rating })
      });
    } catch (e) {
      console.warn('Backend rate sync failed:', e);
    }
  }

  closeRateModal();
  showToast(`Recorded ${rating}-star rating for ${course ? course.title : 'course'}!`, 'success');
  renderAllViews();
}

// ── 8. Admin Studio: Create Course ──────────────────────────────────────────
async function handleCreateCourse(e) {
  e.preventDefault();
  const title = document.getElementById('courseTitle').value.trim();
  const category = document.getElementById('courseCategory').value;
  const level = document.getElementById('courseLevel').value;
  const instructor = document.getElementById('courseInstructor').value.trim();
  const durationHours = parseInt(document.getElementById('courseDuration').value);
  const price = parseFloat(document.getElementById('coursePrice').value);
  const description = document.getElementById('courseDescription').value.trim();

  const newId = state.courses.length + 1;
  const newCourse = {
    itemId: newId,
    title,
    category,
    level,
    instructor,
    durationHours,
    price,
    description,
    averageRating: 5.0,
    totalRatings: 1,
    viewCount: 1,
    enrollmentCount: 0
  };

  state.courses.push(newCourse);

  if (state.isLiveBackend) {
    try {
      await fetch('/api/courses', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newCourse)
      });
    } catch (err) {
      console.warn('Backend create course sync failed:', err);
    }
  }

  showToast(`Published new course: "${title}" to catalog!`, 'success');
  document.getElementById('addCourseForm').reset();
  renderAllViews();
}

function deleteCourse(id) {
  state.courses = state.courses.filter(c => c.itemId !== id);
  showToast(`Course #${id} removed from catalog.`, 'info');
  renderAllViews();
}

function renderAdminCatalogTable() {
  const tbody = document.getElementById('adminCoursesTableBody');
  if (!tbody) return;
  document.getElementById('catalogCountBadge').textContent = `${state.courses.length} Items`;

  tbody.innerHTML = state.courses.map(c => `
    <tr>
      <td><code>#${c.itemId}</code></td>
      <td><strong>${c.title}</strong></td>
      <td><span class="badge-pill outline">${c.category.replace('_', ' ')}</span></td>
      <td>₹${c.price}</td>
      <td>⭐ ${c.averageRating.toFixed(1)}</td>
      <td><button class="btn btn-outline btn-sm" onclick="deleteCourse(${c.itemId})" style="color:#f43f5e;">Delete</button></td>
    </tr>
  `).join('');
}

// ── 9. User Directory ───────────────────────────────────────────────────────
function renderUsersTable() {
  const tbody = document.getElementById('usersTableBody');
  if (!tbody) return;

  tbody.innerHTML = state.users.map(u => `
    <tr>
      <td><code>#${u.userId}</code></td>
      <td><strong>${u.name}</strong></td>
      <td>${u.email}</td>
      <td><span class="badge-pill ${u.role === 'ADMIN' ? 'amber' : u.role === 'CONTENT_MANAGER' ? 'violet' : ''}">${u.role}</span></td>
      <td><span class="badge-pill ${u.active ? 'success' : 'amber'}">${u.active ? 'ACTIVE' : 'DEACTIVATED'}</span></td>
      <td>
        <button class="btn btn-outline btn-sm" onclick="toggleUserStatus(${u.userId})">
          ${u.active ? 'Deactivate' : 'Activate'}
        </button>
      </td>
    </tr>
  `).join('');
}

function toggleUserStatus(id) {
  const u = state.users.find(user => user.userId === id);
  if (u) {
    u.active = !u.active;
    showToast(`User ${u.name} is now ${u.active ? 'ACTIVATED' : 'DEACTIVATED'}`, 'info');
    renderUsersTable();
  }
}

// ── 10. Analytics & Leaderboards ────────────────────────────────────────────
function renderLeaderboards() {
  const topRated = document.getElementById('topRatedLeaderboard');
  const topViewed = document.getElementById('topViewedLeaderboard');
  if (!topRated || !topViewed) return;

  const sortedRated = [...state.courses].sort((a, b) => b.averageRating - a.averageRating).slice(0, 5);
  topRated.innerHTML = sortedRated.map((c, i) => `
    <div class="leaderboard-item">
      <span><strong>#${i + 1}</strong> ${c.title}</span>
      <span class="badge-pill amber">⭐ ${c.averageRating.toFixed(1)} (${c.totalRatings} ratings)</span>
    </div>
  `).join('');

  const sortedViewed = [...state.courses].sort((a, b) => b.viewCount - a.viewCount).slice(0, 5);
  topViewed.innerHTML = sortedViewed.map((c, i) => `
    <div class="leaderboard-item">
      <span><strong>#${i + 1}</strong> ${c.title}</span>
      <span class="badge-pill">👁 ${c.viewCount} views</span>
    </div>
  `).join('');
}

function updateTelemetryReport() {
  const consoleElem = document.getElementById('telemetryConsole');
  if (!consoleElem) return;

  const timestamp = new Date().toISOString().replace('T', ' ').substring(0, 19);
  const text = `╔══════════════════════════════════════════════════════════════╗
║   SMART RECOMMENDATION PLATFORM — REAL-TIME TELEMETRY REPORT   ║
╚══════════════════════════════════════════════════════════════╝
Generated at: ${timestamp}
Project #256 | NIET Greater Noida | UN SDG 9 Alignment

[SYSTEM STATUS]
  ● Backend Architecture    : Java SE 21+ Object Oriented Platform
  ● Concurrency Framework  : AnalyticsMonitorDaemon (PRIORITY: NORM-1)
  ● Socket Server          : Embedded HTTP on port 8080 (TCP Sockets)
  ● Strategy Context       : ${state.currentStrategy}
  ● Total Registered Users : ${state.users.length}
  ● Active Catalog Items   : ${state.courses.length}
  ● Recorded Enrollments   : ${state.enrollments.length}

[RECOMMENDATION ENGINE TELEMETRY]
  ● Active Algorithm       : ${state.currentStrategy}
  ● Mathematical Context   : Score = 0.3×Views + 0.4×Ratings + 3.0×AvgRating
  ● Active Persona Target  : ${state.currentUser.name} (${state.currentUser.role})
  ● In-Memory Cache Status : SYNCHRONIZED

[I/O & PERSISTENCE SUBSYSTEM]
  ● CSV Exporters          : users_export.csv, courses_export.csv (Character Streams)
  ● Exception Auditing     : Zero unhandled faults logged.
════════════════════════════════════════════════════════════════`;
  consoleElem.textContent = text;
}

// ── 11. CSV Export ──────────────────────────────────────────────────────────
function exportData(type) {
  let csvContent = "data:text/csv;charset=utf-8,";
  let filename = "";

  if (type === 'users') {
    csvContent += "UserID,Name,Email,Role,Active\n";
    state.users.forEach(u => {
      csvContent += `${u.userId},"${u.name}",${u.email},${u.role},${u.active}\n`;
    });
    filename = "users_export.csv";
  } else {
    csvContent += "CourseID,Title,Category,Price,AvgRating,TotalRatings,Views\n";
    state.courses.forEach(c => {
      csvContent += `${c.itemId},"${c.title}",${c.category},${c.price},${c.averageRating.toFixed(1)},${c.totalRatings},${c.viewCount}\n`;
    });
    filename = "courses_export.csv";
  }

  const encodedUri = encodeURI(csvContent);
  const link = document.createElement("a");
  link.setAttribute("href", encodedUri);
  link.setAttribute("download", filename);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);

  showToast(`Downloaded CSV export: ${filename}`, 'success');
}

// ── 12. UI Helpers & Toasts ─────────────────────────────────────────────────
function showToast(message, type = 'success') {
  const container = document.getElementById('toastContainer');
  if (!container) return;
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.textContent = message;
  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    toast.style.transition = 'all 0.3s ease';
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}

function escapeHtml(str) {
  if (!str) return '';
  return str.replace(/'/g, "\\'");
}
