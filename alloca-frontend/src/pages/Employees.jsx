import { useState, useEffect, useCallback } from 'react';
import {
  Box, Button, Card, CardContent, Typography, TextField, Dialog, DialogTitle,
  DialogContent, DialogActions, IconButton, Tooltip, Chip, InputAdornment,
  CircularProgress, Alert, Snackbar, useTheme,
} from '@mui/material';
import {
  Add as AddIcon, Edit as EditIcon, Delete as DeleteIcon, Search as SearchIcon,
  Person as PersonIcon, Build as BuildIcon, FindInPage as FindInPageIcon,
} from '@mui/icons-material';
import { DataGrid } from '@mui/x-data-grid';
import { motion, AnimatePresence } from 'framer-motion';
import { getEmployees, createEmployee, updateEmployee, deleteEmployee, getEmployeeSkills, addEmployeeSkills, searchEmployeesBySkill } from '../api/api';

const initialForm = { employeeCode: '', fullName: '', email: '', role: '', department: '' };

export default function Employees() {
  const theme = useTheme();
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [form, setForm] = useState(initialForm);
  const [editing, setEditing] = useState(null);
  const [search, setSearch] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [errors, setErrors] = useState({});
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const [deleteTarget, setDeleteTarget] = useState(null);

  // Skill Management State
  const [skillsOpen, setSkillsOpen] = useState(false);
  const [skillsTarget, setSkillsTarget] = useState(null);
  const [skillsList, setSkillsList] = useState([]);
  const [newSkill, setNewSkill] = useState('');
  const [skillsLoading, setSkillsLoading] = useState(false);

  // Resource Search State
  const [searchSkillOpen, setSearchSkillOpen] = useState(false);
  const [searchSkillInput, setSearchSkillInput] = useState('');
  const [searchSkillResults, setSearchSkillResults] = useState([]);
  const [searchSkillLoading, setSearchSkillLoading] = useState(false);

  const fetchEmployees = useCallback(async () => {
    try {
      const data = await getEmployees();
      setEmployees(data);
    } catch (err) {
      console.error('Failed to load employees:', err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchEmployees(); }, [fetchEmployees]);

  const validate = () => {
    const errs = {};
    if (!form.fullName.trim()) errs.fullName = 'Full Name is required';
    if (!form.email.trim()) {
      errs.email = 'Email is required';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
      errs.email = 'Invalid email format';
    }
    if (!form.role.trim()) errs.role = 'Role is required';
    if (!form.department.trim()) errs.department = 'Department is required';
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleOpenAdd = () => {
    setForm(initialForm);
    setEditing(null);
    setErrors({});
    setDialogOpen(true);
  };

  const handleOpenEdit = (emp) => {
    setForm({
      employeeCode: emp.employeeCode,
      fullName: emp.fullName,
      email: emp.email,
      role: emp.role,
      department: emp.department,
    });
    setEditing(emp);
    setErrors({});
    setDialogOpen(true);
  };

  const handleClose = () => {
    setDialogOpen(false);
    setForm(initialForm);
    setEditing(null);
    setErrors({});
  };

  const handleSubmit = async () => {
    if (!validate()) return;
    setSubmitting(true);
    try {
      if (editing) {
        await updateEmployee(editing.id, form);
        setSnackbar({ open: true, message: 'Employee updated successfully', severity: 'success' });
      } else {
        await createEmployee(form);
        setSnackbar({ open: true, message: 'Employee created successfully', severity: 'success' });
      }
      handleClose();
      await fetchEmployees();
    } catch (err) {
      setSnackbar({ open: true, message: 'Operation failed', severity: 'error' });
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteClick = (emp) => {
    setDeleteTarget(emp);
    setDeleteOpen(true);
  };

  const handleDeleteConfirm = async () => {
    if (!deleteTarget) return;
    try {
      await deleteEmployee(deleteTarget.id);
      setSnackbar({ open: true, message: 'Employee deleted successfully', severity: 'success' });
      setDeleteOpen(false);
      setDeleteTarget(null);
      await fetchEmployees();
    } catch (err) {
      setSnackbar({ open: true, message: 'Delete failed', severity: 'error' });
    }
  };

  const filtered = employees.filter(
    (e) =>
      e.employeeCode.toLowerCase().includes(search.toLowerCase()) ||
      e.fullName.toLowerCase().includes(search.toLowerCase()) ||
      e.email.toLowerCase().includes(search.toLowerCase()) ||
      e.role.toLowerCase().includes(search.toLowerCase()) ||
      e.department.toLowerCase().includes(search.toLowerCase())
  );

  const handleOpenSkills = async (emp) => {
    setSkillsTarget(emp);
    setSkillsList([]);
    setSkillsOpen(true);
    setSkillsLoading(true);
    try {
      const skills = await getEmployeeSkills(emp.employeeId || emp.id);
      setSkillsList(skills || []);
    } catch (err) {
      setSnackbar({ open: true, message: 'Failed to load skills', severity: 'error' });
    } finally {
      setSkillsLoading(false);
    }
  };

  const handleAddSkill = async () => {
    if (!newSkill.trim()) return;
    const addedSkills = [...new Set([...skillsList, newSkill.trim()])];
    setSkillsLoading(true);
    try {
      const updated = await addEmployeeSkills(skillsTarget.employeeId || skillsTarget.id, [newSkill.trim()]);
      setSkillsList(updated);
      setNewSkill('');
      setSnackbar({ open: true, message: 'Skill added successfully', severity: 'success' });
    } catch (err) {
      setSnackbar({ open: true, message: 'Failed to add skill', severity: 'error' });
    } finally {
      setSkillsLoading(false);
    }
  };

  const handleSearchBySkill = async () => {
    if (!searchSkillInput.trim()) return;
    setSearchSkillLoading(true);
    try {
      const results = await searchEmployeesBySkill(searchSkillInput.trim());
      setSearchSkillResults(results || []);
    } catch (err) {
      setSnackbar({ open: true, message: 'Search failed', severity: 'error' });
    } finally {
      setSearchSkillLoading(false);
    }
  };

  const columns = [
    { field: 'employeeCode', headerName: 'Code', width: 110, renderCell: (params) => (
      <Chip label={params.value} size="small" variant="outlined" color="primary" />
    )},
    { field: 'fullName', headerName: 'Full Name', flex: 1, minWidth: 150 },
    { field: 'email', headerName: 'Email', flex: 1, minWidth: 180 },
    { field: 'role', headerName: 'Role', width: 130 },
    { field: 'department', headerName: 'Department', width: 140, renderCell: (params) => (
      <Chip label={params.value} size="small" color="secondary" variant="outlined" />
    )},
    {
      field: 'actions',
      headerName: 'Actions',
      width: 140,
      sortable: false,
      renderCell: (params) => (
        <Box>
          <Tooltip title="Manage Skills">
            <IconButton size="small" onClick={() => handleOpenSkills(params.row)} sx={{ color: theme.palette.info.main }}>
              <BuildIcon fontSize="small" />
            </IconButton>
          </Tooltip>
          <Tooltip title="Edit">
            <IconButton size="small" onClick={() => handleOpenEdit(params.row)} sx={{ color: theme.palette.primary.main }}>
              <EditIcon fontSize="small" />
            </IconButton>
          </Tooltip>
          <Tooltip title="Delete">
            <IconButton size="small" onClick={() => handleDeleteClick(params.row)} sx={{ color: theme.palette.error.main }}>
              <DeleteIcon fontSize="small" />
            </IconButton>
          </Tooltip>
        </Box>
      ),
    },
  ];

  return (
    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ duration: 0.3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" sx={{ fontWeight: 800 }}>Employees</Typography>
        <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={handleOpenAdd}
          >
            Add Employee
          </Button>
        </motion.div>
      </Box>

      <Card>
        <CardContent>
          <Box sx={{ mb: 2, display: 'flex', gap: 2 }}>
            <TextField
              size="small"
              placeholder="Search employees..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start"><SearchIcon /></InputAdornment>
                ),
              }}
              sx={{ maxWidth: 400, flex: 1 }}
            />
            <Button
              variant="outlined"
              startIcon={<FindInPageIcon />}
              onClick={() => { setSearchSkillResults([]); setSearchSkillInput(''); setSearchSkillOpen(true); }}
            >
              Find by Skill
            </Button>
          </Box>
          <DataGrid
            rows={filtered}
            columns={columns}
            loading={loading}
            getRowId={(row) => row.employeeId || row.id}
            pageSizeOptions={[5, 10, 25]}
            initialState={{ pagination: { paginationModel: { pageSize: 10 } } }}
            disableRowSelectionOnClick
            autoHeight
            sx={{
              border: 'none',
              '& .MuiDataGrid-cell': { py: 1.5 },
              '& .MuiDataGrid-columnHeaders': {
                backgroundColor: theme.palette.mode === 'dark' ? 'rgba(255,255,255,0.03)' : 'rgba(103,80,164,0.04)',
                borderRadius: 1,
              },
              '& .MuiDataGrid-row': {
                transition: 'background-color 0.2s',
                '&:hover': { backgroundColor: theme.palette.mode === 'dark' ? 'rgba(255,255,255,0.04)' : 'rgba(103,80,164,0.03)' },
              },
            }}
          />
        </CardContent>
      </Card>

      {/* Add/Edit Dialog */}
      <AnimatePresence>
        {dialogOpen && (
          <Dialog
            open={dialogOpen}
            onClose={handleClose}
            maxWidth="sm"
            fullWidth
            sx={{ '& .MuiPaper-root': { borderRadius: '16px !important', overflow: 'hidden' } }}
          >
            <DialogTitle sx={{
              fontWeight: 700,
              fontSize: '1.25rem',
              background: `linear-gradient(135deg, ${theme.palette.primary.main} 0%, ${theme.palette.primary.dark} 100%)`,
              color: '#fff',
              px: 3,
              py: 2.5,
              display: 'flex',
              alignItems: 'center',
              gap: 1.5,
            }}>
              <PersonIcon sx={{ fontSize: 28, opacity: 0.9 }} />
              {editing ? 'Edit Employee' : 'Add Employee'}
            </DialogTitle>
            <DialogContent>
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
                <TextField
                  label="Employee Code"
                  value={form.employeeCode}
                  onChange={(e) => setForm({ ...form, employeeCode: e.target.value })}
                  placeholder="Auto-generated (EMP008...)"
                  disabled
                  fullWidth
                  helperText={form.employeeCode ? `Code: ${form.employeeCode}` : 'Auto-generated on save'}
                  sx={{
                    '& .MuiInputBase-root.Mui-disabled': {
                      '& .MuiOutlinedInput-notchedOutline': {
                        borderColor: 'rgba(103, 80, 164, 0.15)',
                        borderStyle: 'dashed',
                      },
                    },
                    '& .MuiInputLabel-root.Mui-disabled': {
                      color: 'text.secondary',
                    },
                    '& .MuiFormHelperText-root': {
                      color: 'text.secondary',
                      fontStyle: 'italic',
                    },
                  }}
                />
                <TextField
                  label="Full Name"
                  value={form.fullName}
                  onChange={(e) => setForm({ ...form, fullName: e.target.value })}
                  error={!!errors.fullName}
                  helperText={errors.fullName}
                  fullWidth
                />
                <TextField
                  label="Email"
                  type="email"
                  value={form.email}
                  onChange={(e) => setForm({ ...form, email: e.target.value })}
                  error={!!errors.email}
                  helperText={errors.email}
                  fullWidth
                />
                <TextField
                  label="Role"
                  value={form.role}
                  onChange={(e) => setForm({ ...form, role: e.target.value })}
                  error={!!errors.role}
                  helperText={errors.role}
                  fullWidth
                />
                <TextField
                  label="Department"
                  value={form.department}
                  onChange={(e) => setForm({ ...form, department: e.target.value })}
                  error={!!errors.department}
                  helperText={errors.department}
                  fullWidth
                />
              </Box>
            </DialogContent>
            <DialogActions sx={{ px: 3, pb: 2 }}>
              <Button onClick={handleClose} color="inherit">Cancel</Button>
              <Button
                onClick={handleSubmit}
                variant="contained"
                disabled={submitting}
                startIcon={submitting ? <CircularProgress size={18} /> : null}
              >
                {editing ? 'Update' : 'Create'}
              </Button>
            </DialogActions>
          </Dialog>
        )}
      </AnimatePresence>

      {/* Delete Confirmation */}
      <Dialog open={deleteOpen} onClose={() => setDeleteOpen(false)} maxWidth="xs">
        <DialogTitle sx={{ fontWeight: 700 }}>Confirm Delete</DialogTitle>
        <DialogContent>
          <Typography>
            Are you sure you want to delete <strong>{deleteTarget?.fullName}</strong>?
          </Typography>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setDeleteOpen(false)} color="inherit">Cancel</Button>
          <Button onClick={handleDeleteConfirm} variant="contained" color="error">
            Delete
          </Button>
        </DialogActions>
      </Dialog>

      {/* Skill Management Dialog */}
      <Dialog open={skillsOpen} onClose={() => setSkillsOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>
          Manage Skills - {skillsTarget?.fullName}
        </DialogTitle>
        <DialogContent sx={{ minHeight: 150 }}>
          {skillsLoading && skillsList.length === 0 ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
              <CircularProgress />
            </Box>
          ) : (
            <Box sx={{ pt: 1, display: 'flex', flexDirection: 'column', gap: 3 }}>
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                {skillsList.length > 0 ? (
                  skillsList.map((skill, index) => (
                    <Chip key={index} label={skill} color="primary" variant="outlined" />
                  ))
                ) : (
                  <Typography variant="body2" color="text.secondary">No skills found.</Typography>
                )}
              </Box>
              <Box sx={{ display: 'flex', gap: 1 }}>
                <TextField
                  size="small"
                  label="New Skill"
                  fullWidth
                  value={newSkill}
                  onChange={(e) => setNewSkill(e.target.value)}
                  onKeyDown={(e) => { if (e.key === 'Enter') handleAddSkill(); }}
                />
                <Button variant="contained" onClick={handleAddSkill} disabled={skillsLoading || !newSkill.trim()}>
                  Add
                </Button>
              </Box>
            </Box>
          )}
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setSkillsOpen(false)}>Close</Button>
        </DialogActions>
      </Dialog>

      {/* Resource Search Dialog */}
      <Dialog open={searchSkillOpen} onClose={() => setSearchSkillOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>Find Resource by Skill</DialogTitle>
        <DialogContent sx={{ minHeight: 250 }}>
          <Box sx={{ pt: 1, display: 'flex', flexDirection: 'column', gap: 3 }}>
            <Box sx={{ display: 'flex', gap: 1 }}>
              <TextField
                size="small"
                label="Skill (e.g. Java)"
                fullWidth
                value={searchSkillInput}
                onChange={(e) => setSearchSkillInput(e.target.value)}
                onKeyDown={(e) => { if (e.key === 'Enter') handleSearchBySkill(); }}
              />
              <Button variant="contained" onClick={handleSearchBySkill} disabled={searchSkillLoading || !searchSkillInput.trim()}>
                Search
              </Button>
            </Box>
            <Box>
              {searchSkillLoading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}><CircularProgress /></Box>
              ) : (
                <Box>
                  {searchSkillResults.length > 0 ? (
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                      <Typography variant="subtitle2" color="text.secondary" sx={{ mb: 1 }}>Search Results:</Typography>
                      {searchSkillResults.map((res, index) => (
                        <Card key={index} variant="outlined" sx={{ display: 'flex', justifyContent: 'space-between', p: 1.5, alignItems: 'center' }}>
                          <Typography fontWeight={600}>{res.employeeName}</Typography>
                          <Typography variant="body2" color={res.available > 0 ? 'success.main' : 'error.main'} fontWeight={600}>
                            Available: {res.available}%
                          </Typography>
                        </Card>
                      ))}
                    </Box>
                  ) : (
                    <Typography variant="body2" color="text.secondary" sx={{ textAlign: 'center', py: 4 }}>
                      {searchSkillInput ? 'No resources found with this skill.' : 'Enter a skill to search.'}
                    </Typography>
                  )}
                </Box>
              )}
            </Box>
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setSearchSkillOpen(false)}>Close</Button>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        <Alert severity={snackbar.severity} variant="filled" sx={{ borderRadius: 2 }}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </motion.div>
  );
}
