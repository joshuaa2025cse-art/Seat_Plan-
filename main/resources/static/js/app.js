/**
 * SeatPlan - Exam Hall Seating Arrangement Generator
 * Frontend Logic & Interactive Engine
 */

const API_BASE = '/api';

// Application State
const state = {
    sessions: [],
    halls: [],
    students: [],
    departments: [],
    currentSessionId: null,
    currentHallId: null,
    currentSeats: [],
    activeTab: 'visualizer'
};

// DOM Ready initialization
document.addEventListener('DOMContentLoaded', () => {
    initApp();
});

async function initApp() {
    setupEventListeners();
    await loadInitialData();
}

function setupEventListeners() {
    // Tab switching
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const targetTab = e.currentTarget.dataset.tab;
            switchTab(targetTab);
        });
    });

    // Session & Hall Selectors
    const sessionSelect = document.getElementById('sessionSelect');
    if (sessionSelect) {
        sessionSelect.addEventListener('change', (e) => {
            state.currentSessionId = Number(e.target.value);
            refreshHallView();
        });
    }

    const hallSelect = document.getElementById('hallSelect');
    if (hallSelect) {
        hallSelect.addEventListener('change', (e) => {
            state.currentHallId = Number(e.target.value);
            refreshHallView();
        });
    }

    // Instant Seat Search
    const searchInput = document.getElementById('seatSearchInput');
    if (searchInput) {
        let debounceTimer;
        searchInput.addEventListener('input', (e) => {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(() => {
                handleSeatSearch(e.target.value);
            }, 300);
        });
    }
}

// Initial Data Fetch
async function loadInitialData() {
    try {
        const [sessionsRes, hallsRes, deptsRes, studentsRes] = await Promise.all([
            fetch(`${API_BASE}/sessions`),
            fetch(`${API_BASE}/halls`),
            fetch(`${API_BASE}/students/departments`),
            fetch(`${API_BASE}/students`)
        ]);

        state.sessions = await sessionsRes.json();
        state.halls = await hallsRes.json();
        state.departments = await deptsRes.json();
        state.students = await studentsRes.json();

        populateSessionDropdown();
        populateHallDropdown();
        updateStats();

        if (state.sessions.length > 0 && !state.currentSessionId) {
            state.currentSessionId = state.sessions[0].id;
            sessionSelect.value = state.currentSessionId;
        }

        if (state.halls.length > 0 && !state.currentHallId) {
            state.currentHallId = state.halls[0].id;
            hallSelect.value = state.currentHallId;
        }

        await refreshHallView();
    } catch (err) {
        console.error('Failed to load initial data:', err);
        showToast('Error connecting to backend API: ' + err.message, 'error');
    }
}

function populateSessionDropdown() {
    const select = document.getElementById('sessionSelect');
    if (!select) return;
    select.innerHTML = state.sessions.map(s => 
        `<option value="${s.id}">${s.sessionName} (${s.subjectCode})</option>`
    ).join('');
    if (state.currentSessionId) select.value = state.currentSessionId;
}

function populateHallDropdown() {
    const select = document.getElementById('hallSelect');
    if (!select) return;
    select.innerHTML = state.halls.map(h => 
        `<option value="${h.id}">${h.hallNumber} - ${h.name} (${h.capacity} Desks)</option>`
    ).join('');
    if (state.currentHallId) select.value = state.currentHallId;
}

function updateStats() {
    const totalHallsElem = document.getElementById('statTotalHalls');
    const totalCapacityElem = document.getElementById('statTotalCapacity');
    const totalStudentsElem = document.getElementById('statTotalStudents');
    const totalDeptsElem = document.getElementById('statTotalDepts');

    if (totalHallsElem) totalHallsElem.textContent = state.halls.length;
    if (totalCapacityElem) {
        const cap = state.halls.reduce((acc, h) => acc + h.capacity, 0);
        totalCapacityElem.textContent = cap;
    }
    if (totalStudentsElem) totalStudentsElem.textContent = state.students.length;
    if (totalDeptsElem) totalDeptsElem.textContent = state.departments.length;
}

// Refresh Seating Grid View
async function refreshHallView() {
    if (!state.currentHallId || !state.currentSessionId) {
        return;
    }

    const currentHall = state.halls.find(h => h.id === state.currentHallId);
    const currentSession = state.sessions.find(s => s.id === state.currentSessionId);

    // Update Hall details display
    const hallTitleElem = document.getElementById('currentHallTitle');
    const hallMetaElem = document.getElementById('currentHallMeta');
    if (hallTitleElem && currentHall) {
        hallTitleElem.textContent = `${currentHall.hallNumber}: ${currentHall.name}`;
    }
    if (hallMetaElem && currentHall && currentSession) {
        hallMetaElem.textContent = `Declared Capacity: ${currentHall.capacity} Desks (${currentHall.totalRows} Rows × ${currentHall.totalColumns} Cols) | Session: ${currentSession.sessionName}`;
    }

    try {
        const res = await fetch(`${API_BASE}/seatplan/session/${state.currentSessionId}/hall/${state.currentHallId}`);
        state.currentSeats = await res.json();
        renderSeatingGrid(currentHall, state.currentSeats);
    } catch (err) {
        console.error('Failed to load hall seats:', err);
        showToast('Error loading hall seats', 'error');
    }
}

// Render Hall Visualizer Grid
function renderSeatingGrid(hall, seats) {
    const gridContainer = document.getElementById('hallGrid');
    if (!gridContainer || !hall) return;

    gridContainer.style.gridTemplateColumns = `repeat(${hall.totalColumns}, 140px)`;
    gridContainer.innerHTML = '';

    // Create a lookup map for seats: "row-col" -> seat
    const seatMap = new Map();
    seats.forEach(s => {
        seatMap.set(`${s.rowNum}-${s.colNum}`, s);
    });

    for (let r = 1; r <= hall.totalRows; r++) {
        for (let c = 1; c <= hall.totalColumns; c++) {
            const seat = seatMap.get(`${r}-${c}`);
            const seatElem = createSeatCard(hall, r, c, seat);
            gridContainer.appendChild(seatElem);
        }
    }
}

function createSeatCard(hall, row, col, seat) {
    const card = document.createElement('div');
    card.className = 'seat-card';
    card.id = `seat-node-${row}-${col}`;

    if (!seat || !seat.occupied) {
        card.classList.add('vacant');
        card.innerHTML = `
            <div class="seat-top">
                <span class="seat-number">${hall.hallNumber}-R${row}C${col}</span>
                <span class="seat-badge" style="background: rgba(255,255,255,0.06); color: #94A3B8;">VACANT</span>
            </div>
            <div class="seat-mid" style="text-align: center; color: #64748B; font-size: 0.8rem; margin: 0.8rem 0;">
                <span>Available</span>
            </div>
            <div class="seat-bottom">
                <span>Row ${row}</span>
                <span>Col ${col}</span>
            </div>
        `;
        return card;
    }

    // Determine department color class & badge
    const dept = seat.studentDepartment || '';
    const section = seat.studentSection || 'A';
    let deptClass = 'dept-other';
    let badgeClass = 'other';
    let deptShort = 'GEN';

    if (dept.includes('Computer')) {
        deptClass = 'dept-cs';
        badgeClass = 'cs';
        deptShort = 'CS-' + section;
    } else if (dept.includes('Electronics')) {
        deptClass = 'dept-ec';
        badgeClass = 'ec';
        deptShort = 'EC-' + section;
    } else if (dept.includes('Mechanical')) {
        deptClass = 'dept-me';
        badgeClass = 'me';
        deptShort = 'ME-' + section;
    } else if (dept.includes('Civil')) {
        deptClass = 'dept-ce';
        badgeClass = 'ce';
        deptShort = 'CE-' + section;
    }

    card.classList.add(deptClass);
    card.innerHTML = `
        <div class="seat-top">
            <span class="seat-number">${seat.seatNumber}</span>
            <span class="seat-badge ${badgeClass}">${deptShort}</span>
        </div>
        <div class="seat-mid">
            <div class="student-name" title="${seat.studentName}">${seat.studentName}</div>
            <div class="student-roll">${seat.studentRollNumber}</div>
        </div>
        <div class="seat-bottom">
            <span>R${row}·C${col}</span>
            <span style="font-size: 0.65rem; color: #10B981;">Sec ${section}</span>
        </div>
    `;

    card.addEventListener('click', () => {
        showSeatDetailModal(seat);
    });

    return card;
}

// Seat Detail Modal
function showSeatDetailModal(seat) {
    const modal = document.getElementById('seatDetailModal');
    const content = document.getElementById('seatDetailContent');
    if (!modal || !content) return;

    content.innerHTML = `
        <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 1rem;">
            <div style="width: 52px; height: 52px; border-radius: 12px; background: linear-gradient(135deg, var(--primary), var(--secondary)); display: flex; align-items: center; justify-content: center; font-size: 1.5rem; font-weight: 700;">
                ${seat.studentName ? seat.studentName.charAt(0) : 'S'}
            </div>
            <div>
                <h4 style="font-size: 1.15rem; color: #FFF; margin: 0;">${seat.studentName}</h4>
                <span style="font-family: var(--font-mono); color: var(--secondary); font-size: 0.85rem;">${seat.studentRollNumber}</span>
            </div>
        </div>

        <div style="background: rgba(255,255,255,0.03); border: 1px solid var(--border-subtle); border-radius: 10px; padding: 1rem; display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem; font-size: 0.85rem;">
            <div>
                <span style="color: var(--text-muted); display: block; font-size: 0.72rem; text-transform: uppercase;">Hall Number</span>
                <strong style="color: #FFF;">${seat.hallNumber} (${seat.hallName})</strong>
            </div>
            <div>
                <span style="color: var(--text-muted); display: block; font-size: 0.72rem; text-transform: uppercase;">Seat Number</span>
                <strong style="color: #22D3EE;">${seat.seatNumber} (Row ${seat.rowNum}, Col ${seat.colNum})</strong>
            </div>
            <div>
                <span style="color: var(--text-muted); display: block; font-size: 0.72rem; text-transform: uppercase;">Class / Section</span>
                <strong style="color: #FFF;">${seat.studentClassSection || (seat.studentDepartment + ' - Sec ' + (seat.studentSection || 'A'))}</strong>
            </div>
            <div>
                <span style="color: var(--text-muted); display: block; font-size: 0.72rem; text-transform: uppercase;">Exam Session</span>
                <strong style="color: #FFF;">${seat.examSessionName}</strong>
            </div>
        </div>
    `;

    openModal('seatDetailModal');
}

// Instant Seat Search Handler
async function handleSeatSearch(query) {
    const resultBox = document.getElementById('searchResultBox');
    if (!query || query.trim().length < 2) {
        if (resultBox) resultBox.style.display = 'none';
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/seatplan/search?query=${encodeURIComponent(query.trim())}`);
        const results = await res.json();

        if (results.length > 0) {
            const first = results[0];
            resultBox.style.display = 'flex';
            resultBox.innerHTML = `
                <div class="search-result-info">
                    <div>
                        <span style="font-size: 0.75rem; color: var(--text-muted);">CANDIDATE:</span>
                        <strong style="color: #FFF; margin-left: 0.35rem;">${first.studentName}</strong>
                        <span style="color: var(--secondary); font-family: var(--font-mono); margin-left: 0.5rem;">(${first.studentRollNumber})</span>
                    </div>
                    <div>
                        <span style="font-size: 0.75rem; color: var(--text-muted);">CLASS & SECTION:</span>
                        <strong style="color: #FFF; margin-left: 0.35rem;">${first.classSection || (first.studentDepartment + ' - ' + first.studentSection)}</strong>
                    </div>
                    <div>
                        <span style="font-size: 0.75rem; color: var(--text-muted);">HALL:</span>
                        <strong style="color: #FFF; margin-left: 0.35rem;">${first.hallNumber}</strong>
                    </div>
                    <div>
                        <span class="search-badge">SEAT ${first.seatNumber}</span>
                    </div>
                </div>
                <button class="btn btn-accent btn-sm" onclick="highlightAndJumpToSeat('${first.hallNumber}', ${first.rowNum}, ${first.colNum}, ${first.examSessionId})">
                    Locate on Grid 🎯
                </button>
            `;
        } else {
            resultBox.style.display = 'flex';
            resultBox.innerHTML = `
                <div style="color: var(--text-muted); font-size: 0.85rem;">
                    No seat allocation found matching "<strong>${query}</strong>".
                </div>
            `;
        }
    } catch (err) {
        console.error('Seat search failed:', err);
    }
}

// Highlight and Jump to Seat in Hall
async function highlightAndJumpToSeat(hallNumber, row, col, sessionId) {
    const hall = state.halls.find(h => h.hallNumber === hallNumber);
    if (!hall) return;

    if (sessionId) {
        state.currentSessionId = sessionId;
        const sessionSelect = document.getElementById('sessionSelect');
        if (sessionSelect) sessionSelect.value = sessionId;
    }

    state.currentHallId = hall.id;
    const hallSelect = document.getElementById('hallSelect');
    if (hallSelect) hallSelect.value = hall.id;

    switchTab('visualizer');
    await refreshHallView();

    // Pulse highlight the targeted seat
    setTimeout(() => {
        const targetNode = document.getElementById(`seat-node-${row}-${col}`);
        if (targetNode) {
            targetNode.scrollIntoView({ behavior: 'smooth', block: 'center' });
            targetNode.classList.add('highlighted-seat');
            setTimeout(() => {
                targetNode.classList.remove('highlighted-seat');
            }, 5000);
        }
    }, 200);
}

// Open Seating Arrangement Generator Modal
function openGeneratorModal() {
    const sessionSelectModal = document.getElementById('genSessionSelect');
    const hallCheckboxes = document.getElementById('genHallCheckboxes');
    const deptCheckboxes = document.getElementById('genDeptCheckboxes');

    if (sessionSelectModal) {
        sessionSelectModal.innerHTML = state.sessions.map(s => 
            `<option value="${s.id}">${s.sessionName} - ${s.subjectName}</option>`
        ).join('');
    }

    if (hallCheckboxes) {
        hallCheckboxes.innerHTML = state.halls.map(h => `
            <label style="display: flex; align-items: center; gap: 0.5rem; font-size: 0.85rem; cursor: pointer;">
                <input type="checkbox" name="genHalls" value="${h.id}" checked>
                <span>${h.hallNumber} (${h.capacity} max capacity)</span>
            </label>
        `).join('');
    }

    if (deptCheckboxes) {
        deptCheckboxes.innerHTML = state.departments.map(d => `
            <label style="display: flex; align-items: center; gap: 0.5rem; font-size: 0.85rem; cursor: pointer;">
                <input type="checkbox" name="genDepts" value="${d}" checked>
                <span>${d}</span>
            </label>
        `).join('');
    }

    openModal('generatorModal');
}

// Execute Seating Plan Generation
async function executeArrangementGeneration() {
    const sessionSelectModal = document.getElementById('genSessionSelect');
    const strategySelect = document.getElementById('genStrategySelect');

    const selectedHallIds = Array.from(document.querySelectorAll('input[name="genHalls"]:checked'))
        .map(cb => Number(cb.value));

    const selectedDepts = Array.from(document.querySelectorAll('input[name="genDepts"]:checked'))
        .map(cb => cb.value);

    if (!sessionSelectModal.value) {
        showToast('Please select an Exam Session', 'error');
        return;
    }

    if (selectedHallIds.length === 0) {
        showToast('Please select at least one Examination Hall', 'error');
        return;
    }

    const payload = {
        examSessionId: Number(sessionSelectModal.value),
        hallIds: selectedHallIds,
        departments: selectedDepts,
        strategy: strategySelect.value
    };

    const submitBtn = document.getElementById('btnSubmitGen');
    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span>⏳ Allocating Seats...</span>';
    }

    try {
        const res = await fetch(`${API_BASE}/seatplan/generate`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await res.json();

        if (res.ok) {
            closeModal('generatorModal');
            showToast(data.message || 'Seating arrangement generated successfully!', 'success');
            await loadInitialData();
            switchTab('visualizer');
        } else {
            showToast(data.error || 'Failed to generate arrangement', 'error');
        }
    } catch (err) {
        console.error('Generation error:', err);
        showToast('Error generating seating arrangement: ' + err.message, 'error');
    } finally {
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<span>⚡ Generate Now</span>';
        }
    }
}

// Tab Switching
function switchTab(tabName) {
    state.activeTab = tabName;
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.toggle('active', btn.dataset.tab === tabName);
    });

    document.querySelectorAll('.tab-view').forEach(view => {
        view.style.display = 'none';
    });

    const targetView = document.getElementById(`tab-view-${tabName}`);
    if (targetView) {
        targetView.style.display = 'block';
    }

    if (tabName === 'visualizer') {
        refreshHallView();
    } else if (tabName === 'halls') {
        renderHallsTable();
    } else if (tabName === 'students') {
        renderStudentsTable();
    } else if (tabName === 'sessions') {
        renderSessionsTable();
    }
}

// Render Halls Management Table
function renderHallsTable() {
    const tbody = document.getElementById('hallsTableBody');
    if (!tbody) return;

    tbody.innerHTML = state.halls.map(h => `
        <tr>
            <td><strong>${h.hallNumber}</strong></td>
            <td>${h.name}</td>
            <td>${h.totalRows} × ${h.totalColumns}</td>
            <td><strong>${h.capacity} Desks</strong></td>
            <td><span class="seat-badge ${h.active ? 'ec' : 'other'}">${h.active ? 'Active' : 'Inactive'}</span></td>
            <td>
                <button class="btn btn-danger btn-sm" onclick="deleteHall(${h.id})">Delete</button>
            </td>
        </tr>
    `).join('');
}

// Render Students Table
function renderStudentsTable() {
    const tbody = document.getElementById('studentsTableBody');
    if (!tbody) return;

    tbody.innerHTML = state.students.map(s => `
        <tr>
            <td><strong style="font-family: var(--font-mono); color: var(--secondary);">${s.rollNumber}</strong></td>
            <td>${s.name}</td>
            <td>${s.department}</td>
            <td><span class="seat-badge cs">Section ${s.section || 'A'}</span></td>
            <td>Sem ${s.semester}</td>
            <td>${s.email || '-'}</td>
            <td>
                <button class="btn btn-danger btn-sm" onclick="deleteStudent(${s.id})">Delete</button>
            </td>
        </tr>
    `).join('');
}

// Render Exam Sessions Table
function renderSessionsTable() {
    const tbody = document.getElementById('sessionsTableBody');
    if (!tbody) return;

    tbody.innerHTML = state.sessions.map(s => `
        <tr>
            <td><strong>${s.sessionName}</strong></td>
            <td>${s.subjectCode} - ${s.subjectName}</td>
            <td>${s.examDate}</td>
            <td>${s.sessionTime}</td>
            <td><span class="seat-badge ${s.status === 'SEATS_ALLOCATED' ? 'ec' : 'cs'}">${s.status}</span></td>
            <td>
                <button class="btn btn-danger btn-sm" onclick="deleteSession(${s.id})">Delete</button>
            </td>
        </tr>
    `).join('');
}

function updateCalculatedCapacity() {
    const rows = Number(document.getElementById('hallTotalRows')?.value) || 0;
    const cols = Number(document.getElementById('hallTotalCols')?.value) || 0;
    const capInput = document.getElementById('hallCapacity');
    if (capInput) {
        capInput.value = rows * cols;
    }
}

// Add Hall Action
async function handleAddHall(e) {
    e.preventDefault();
    const form = e.target;
    const rows = Number(form.totalRows.value);
    const cols = Number(form.totalColumns.value);
    const cap = form.capacity && form.capacity.value ? Number(form.capacity.value) : (rows * cols);
    const payload = {
        hallNumber: form.hallNumber.value.trim().toUpperCase(),
        name: form.name.value.trim(),
        totalRows: rows,
        totalColumns: cols,
        capacity: cap
    };

    try {
        const res = await fetch(`${API_BASE}/halls`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await res.json();
        if (res.ok) {
            closeModal('addHallModal');
            form.reset();
            showToast('Hall registered successfully!', 'success');
            await loadInitialData();
            renderHallsTable();
        } else {
            showToast(data.error || 'Failed to add hall', 'error');
        }
    } catch (err) {
        showToast('Error: ' + err.message, 'error');
    }
}

// Delete Hall
async function deleteHall(id) {
    if (!confirm('Are you sure you want to delete this hall?')) return;
    try {
        const res = await fetch(`${API_BASE}/halls/${id}`, { method: 'DELETE' });
        if (res.ok) {
            showToast('Hall deleted successfully', 'success');
            await loadInitialData();
            renderHallsTable();
        }
    } catch (err) {
        showToast('Error: ' + err.message, 'error');
    }
}

// Add Student Action
async function handleAddStudent(e) {
    e.preventDefault();
    const form = e.target;
    const payload = {
        rollNumber: form.rollNumber.value.trim().toUpperCase(),
        name: form.name.value.trim(),
        department: form.department.value.trim(),
        section: form.section.value.trim().toUpperCase(),
        semester: Number(form.semester.value),
        email: form.email.value.trim()
    };

    try {
        const res = await fetch(`${API_BASE}/students`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await res.json();
        if (res.ok) {
            closeModal('addStudentModal');
            form.reset();
            showToast('Student registered successfully!', 'success');
            await loadInitialData();
            renderStudentsTable();
        } else {
            showToast(data.error || 'Failed to add student', 'error');
        }
    } catch (err) {
        showToast('Error: ' + err.message, 'error');
    }
}

// Delete Student
async function deleteStudent(id) {
    if (!confirm('Are you sure you want to delete this student?')) return;
    try {
        const res = await fetch(`${API_BASE}/students/${id}`, { method: 'DELETE' });
        if (res.ok) {
            showToast('Student deleted', 'success');
            await loadInitialData();
            renderStudentsTable();
        }
    } catch (err) {
        showToast('Error: ' + err.message, 'error');
    }
}

// Add Session Action
async function handleAddSession(e) {
    e.preventDefault();
    const form = e.target;
    const payload = {
        sessionName: form.sessionName.value.trim(),
        subjectCode: form.subjectCode.value.trim().toUpperCase(),
        subjectName: form.subjectName.value.trim(),
        examDate: form.examDate.value,
        sessionTime: form.sessionTime.value.trim()
    };

    try {
        const res = await fetch(`${API_BASE}/sessions`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await res.json();
        if (res.ok) {
            closeModal('addSessionModal');
            form.reset();
            showToast('Exam session created!', 'success');
            await loadInitialData();
            renderSessionsTable();
        } else {
            showToast(data.error || 'Failed to create session', 'error');
        }
    } catch (err) {
        showToast('Error: ' + err.message, 'error');
    }
}

// Delete Session
async function deleteSession(id) {
    if (!confirm('Are you sure you want to delete this session and its seat allocations?')) return;
    try {
        const res = await fetch(`${API_BASE}/sessions/${id}`, { method: 'DELETE' });
        if (res.ok) {
            showToast('Session deleted', 'success');
            await loadInitialData();
            renderSessionsTable();
        }
    } catch (err) {
        showToast('Error: ' + err.message, 'error');
    }
}

// Reset Demo Data
async function resetDemoData() {
    if (!confirm('Reset all records and reload rich sample data (Halls, Students with Sections, Exams, and Seating Plan)?')) return;
    try {
        showToast('Resetting and populating sample data...', 'info');
        const res = await fetch(`${API_BASE}/seatplan/seed-sample-data`, { method: 'POST' });
        if (res.ok) {
            showToast('Realistic sample dataset reloaded successfully!', 'success');
            await loadInitialData();
            refreshHallView();
        }
    } catch (err) {
        showToast('Error resetting data: ' + err.message, 'error');
    }
}

// Official Print View per Hall
function triggerPrint() {
    const hall = state.halls.find(h => h.id === state.currentHallId);
    const session = state.sessions.find(s => s.id === state.currentSessionId);

    const printHeader = document.getElementById('printHeader');
    if (printHeader && hall && session) {
        const allocatedSeats = state.currentSeats.filter(s => s.occupied);
        let attendanceRows = allocatedSeats.map((s, idx) => `
            <tr>
                <td style="border: 1px solid #333; padding: 6px; text-align: center;">${idx + 1}</td>
                <td style="border: 1px solid #333; padding: 6px; font-weight: bold;">${s.seatNumber}</td>
                <td style="border: 1px solid #333; padding: 6px; font-family: monospace;">${s.studentRollNumber}</td>
                <td style="border: 1px solid #333; padding: 6px;">${s.studentName}</td>
                <td style="border: 1px solid #333; padding: 6px;">${s.studentClassSection || (s.studentDepartment + ' - Sec ' + s.studentSection)}</td>
                <td style="border: 1px solid #333; padding: 6px; width: 120px;"></td>
                <td style="border: 1px solid #333; padding: 6px; width: 100px;"></td>
            </tr>
        `).join('');

        printHeader.innerHTML = `
            <div style="text-align: center; margin-bottom: 15px; border-bottom: 2px solid #000; padding-bottom: 10px;">
                <h1 style="font-size: 20px; margin: 0; text-transform: uppercase;">Office of Controller of Examinations</h1>
                <h2 style="font-size: 16px; margin: 4px 0;">Official Examination Hall Seating & Attendance Chart</h2>
                <div style="font-size: 13px; margin-top: 6px;">
                    <strong>Session:</strong> ${session.sessionName} | 
                    <strong>Course:</strong> ${session.subjectCode} - ${session.subjectName} | 
                    <strong>Date:</strong> ${session.examDate} | 
                    <strong>Time:</strong> ${session.sessionTime}
                </div>
                <div style="font-size: 13px; margin-top: 4px;">
                    <strong>Examination Hall:</strong> ${hall.hallNumber} (${hall.name}) | 
                    <strong>Declared Capacity:</strong> ${hall.capacity} Desks | 
                    <strong>Candidates Allocated:</strong> ${allocatedSeats.length}
                </div>
            </div>

            <div style="margin-top: 20px; page-break-before: auto;">
                <h3 style="font-size: 14px; text-transform: uppercase; margin-bottom: 8px;">Hall Invigilation Sign-Off Sheet</h3>
                <table style="width: 100%; border-collapse: collapse; font-size: 11px;">
                    <thead>
                        <tr style="background: #f0f0f0;">
                            <th style="border: 1px solid #333; padding: 6px;">#</th>
                            <th style="border: 1px solid #333; padding: 6px;">Seat No</th>
                            <th style="border: 1px solid #333; padding: 6px;">Roll Number</th>
                            <th style="border: 1px solid #333; padding: 6px;">Student Name</th>
                            <th style="border: 1px solid #333; padding: 6px;">Class / Section</th>
                            <th style="border: 1px solid #333; padding: 6px;">Student Signature</th>
                            <th style="border: 1px solid #333; padding: 6px;">Invigilator Sign</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${attendanceRows}
                    </tbody>
                </table>

                <div style="margin-top: 40px; display: flex; justify-content: space-between; font-size: 12px;">
                    <div>
                        <p>Total Candidates Present: ________</p>
                        <p>Total Candidates Absent: ________</p>
                    </div>
                    <div style="text-align: right;">
                        <p>______________________________________</p>
                        <p><strong>Chief Invigilator Name & Signature</strong></p>
                    </div>
                </div>
            </div>
            <hr style="margin: 20px 0; border: 1px dashed #999;">
        `;
    }

    window.print();
}

// Modal Helpers
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.classList.add('active');
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.classList.remove('active');
}

// Toast Notification
function showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `
        <span>${type === 'success' ? '✅' : type === 'error' ? '❌' : 'ℹ️'}</span>
        <span>${message}</span>
    `;

    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}
