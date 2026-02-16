const state = { logs: [], baseUrl: '', auth: '' };

function b64(s){ return btoa(unescape(encodeURIComponent(s))); }

function setStatus(msg){ document.getElementById('status').textContent = msg; }

async function fetchLogs(){
  const base = document.getElementById('baseUrl').value.trim() || window.location.origin;
  const user = document.getElementById('fUser').value.trim();
  const action = document.getElementById('fAction').value.trim();
  const from = document.getElementById('fFrom').value;
  const to = document.getElementById('fTo').value;

  const username = document.getElementById('authUser').value.trim() || 'auditor';
  const password = document.getElementById('authPass').value.trim() || 'auditor123';
  const auth = 'Basic ' + b64(username + ':' + password);

  const qs = new URLSearchParams();
  if(user) qs.set('user', user);
  if(action) qs.set('action', action);
  if(from) qs.set('from', from);
  if(to) qs.set('to', to);

  const url = base.replace(/\/$/,'') + '/api/audit-logs' + (qs.toString() ? ('?' + qs.toString()) : '');
  setStatus('Loading...');
  try{
    const res = await fetch(url, { headers: { 'Authorization': auth }});
    if(!res.ok) throw new Error('HTTP ' + res.status);
    const data = await res.json();
    state.logs = data;
    state.baseUrl = base;
    state.auth = auth;
    renderTable();
    setStatus('Loaded ' + data.length + ' log(s).');
  }catch(e){
    console.error(e);
    setStatus('Failed to load logs. Check backend is running and credentials are correct. (' + e.message + ')');
  }
}

function renderTable(){
  const tbody = document.getElementById('tbody');
  tbody.innerHTML = '';
  state.logs.forEach(l => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${escapeHtml(l.timestamp || '')}</td>
      <td><span class="badge">${escapeHtml(l.username || '')}</span></td>
      <td>${escapeHtml(l.action || '')}</td>
      <td>${escapeHtml(l.entityType || '')}</td>
      <td>${escapeHtml(l.entityId || '')}</td>
      <td class="mono">${escapeHtml((l.details || '').slice(0, 500))}${(l.details||'').length>500?'…':''}</td>
    `;
    tbody.appendChild(tr);
  });
}

function escapeHtml(s){
  return String(s).replace(/[&<>"']/g, m => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m]));
}

async function exportCsv(){
  const base = state.baseUrl || (document.getElementById('baseUrl').value.trim() || 'http://localhost:8080');
  const user = document.getElementById('fUser').value.trim();
  const action = document.getElementById('fAction').value.trim();
  const from = document.getElementById('fFrom').value;
  const to = document.getElementById('fTo').value;

  const qs = new URLSearchParams();
  if(user) qs.set('user', user);
  if(action) qs.set('action', action);
  if(from) qs.set('from', from);
  if(to) qs.set('to', to);

  const url = base.replace(/\/$/,'') + '/api/audit-logs/export' + (qs.toString() ? ('?' + qs.toString()) : '');
  const a = document.createElement('a');
  a.href = url;
  // Browser will prompt for credentials if not cached; that's fine for demo.
  a.click();
}

document.getElementById('loadBtn').addEventListener('click', fetchLogs);
document.getElementById('exportBtn').addEventListener('click', exportCsv);
document.getElementById('resetBtn').addEventListener('click', () => {
  ['fUser','fAction','fFrom','fTo'].forEach(id => document.getElementById(id).value = '');
  fetchLogs();
});

// Auto load
fetchLogs();


async function postAdmin(path){
  const base = state.baseUrl || (document.getElementById('baseUrl').value.trim() || window.location.origin);
  const username = document.getElementById('authUser').value.trim() || 'admin';
  const password = document.getElementById('authPass').value.trim() || 'admin123';
  const auth = 'Basic ' + b64(username + ':' + password);
  const demoToken = (document.getElementById('demoToken')?.value || '').trim();

  const url = base.replace(/\/$/,'') + path;
  setStatus('Running admin action...');
  try{
    const headers = { 'Authorization': auth };
    if(demoToken) headers['X-Demo-Token'] = demoToken;
    const res = await fetch(url, { method: 'POST', headers });
    if(!res.ok) throw new Error('HTTP ' + res.status);
    const data = await res.json();
    return data;
  }catch(e){
    console.error(e);
    throw e;
  }
}

async function resetDemo(){
  try{
    const data = await postAdmin('/api/demo/admin/reset');
    setStatus('Reset done. DeletedLogs=' + data.deletedLogs + ', ClearedOrders=' + data.clearedOrders + ', SeededLogs=' + data.seededLogs);
    await fetchLogs();
  }catch(e){
    setStatus('Reset failed. Ensure you are logged in as admin (admin/admin123). (' + e.message + ')');
  }
}

async function generateDemo(){
  try{
    const data = await postAdmin('/api/demo/admin/generate?orders=5&eventsPerOrder=4');
    setStatus('Generated. CreatedLogs=' + data.createdLogs + ', CreatedOrders=' + data.createdOrders);
    await fetchLogs();
  }catch(e){
    setStatus('Generate failed. Ensure you are logged in as admin (admin/admin123). (' + e.message + ')');
  }
}


document.getElementById('btnReset')?.addEventListener('click', resetDemo);
document.getElementById('btnGen')?.addEventListener('click', generateDemo);
