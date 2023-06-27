import numpy as np
from scipy.stats import chi2_contingency

n, k = map(int, input().split())

# Считываем категориальный признак и применяем one-hot преобразование
a = np.zeros((n, k))
for i, val in enumerate(map(int, input().split())):
    a[i, val-1] = 1

# Считываем числовой признак
b = np.array(list(map(int, input().split())))

# Считаем таблицу сопряженности
contingency_table = np.zeros((k, 2))
for i in range(k):
    contingency_table[i, 0] = np.sum(a[:, i] == 1)
    contingency_table[i, 1] = np.sum(b * a[:, i])

# Вычисляем коэффициент корреляции Крамера
chi2, p, dof, expected = chi2_contingency(contingency_table)
n_obs = np.sum(contingency_table)
phi2 = chi2/n_obs
r, c = contingency_table.shape
phi2corr = max(0, phi2 - ((r-1)*(c-1))/(n_obs-1))
rcorr = r - ((r-1)**2)/(n_obs-1)
ccorr = c - ((c-1)**2)/(n_obs-1)
corr = np.sqrt(phi2corr / min((rcorr-1), (ccorr-1)))

print(corr)