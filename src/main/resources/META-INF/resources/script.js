console.log("summaryData:", summaryData);

let currentDate = null;
let chart;
let previousScrollX = 0;
let previousScrollY = 0;

const chartCanvas = document.getElementById('summaryChart');
const backBtn = document.getElementById('backBtn');
const chartContainer = document.querySelector('.chart-area');

if (!summaryData || summaryData.length === 0) {
  if (chartCanvas) {
    chartCanvas.style.display = 'none';
  }

  if (backBtn) {
    backBtn.style.display = 'none';
  }

  if (chartContainer) {
    const emptyState = document.createElement('div');
    emptyState.style.textAlign = 'center';
    emptyState.style.color = '#666';
    emptyState.style.padding = '2rem';
    emptyState.textContent = 'No hay reportes disponibles.';
    chartContainer.appendChild(emptyState);
  } else {
    console.warn('No chart container element found to display empty state.');
  }

  window.resetToDailyView = () => {};
} else {
  const uniqueDates = [...new Set(summaryData.map(d =>
    new Date(d.uploadTime).toISOString().split('T')[0]
  ))];

  const datePicker = document.getElementById('datePicker');
  const today = new Date().toISOString().split('T')[0];

  const minDate = uniqueDates[0];
  const maxDate = uniqueDates[uniqueDates.length - 1];
  datePicker.min = minDate;
  datePicker.max = maxDate;

  const defaultDate = uniqueDates.includes(today) ? today : minDate;
  datePicker.value = defaultDate;
  currentDate = defaultDate;

  function renderChart(data, title) {
    if (chart) chart.destroy();
    const ctx = chartCanvas.getContext('2d');

    chart = new Chart(ctx, {
      type: 'line',
      data: {
        datasets: [{
          label: title,
          data: data.map(d => ({
            x: d.uploadTime,
            y: d.percentage,
            reportName: d.reportName
          })),
          fill: false,
          borderColor: 'red',
          backgroundColor: 'rgba(255,0,0,0.2)',
          borderWidth: 2,
          pointRadius: 5,
          tension: 0.3
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        onClick: (evt, elements) => {
          if (elements.length > 0) {
            const idx = elements[0].index;
            const name = chart.data.datasets[0].data[idx].reportName;
            window.location.href = '/report-detail?name=' + encodeURIComponent(name);
          }
        },
        scales: {
          x: {
            type: 'time',
            time: { unit: 'minute', displayFormats: { minute: 'HH:mm' } },
            title: { display: true, text: 'Hora' }
          },
          y: {
            beginAtZero: true,
            suggestedMax: Math.max(...data.map(d => d.percentage), 10),
            title: { display: true, text: '% Indisponibilidad' }
          }
        },
        plugins: {
          title: { display: true, text: title }
        }
      }
    });

    chartCanvas.onclick = function (evt) {
      const points = chart.getElementsAtEventForMode(evt, 'nearest', { intersect: false }, true);
      if (points.length > 0) return;

      const xScale = chart.scales.x;
      const xValue = xScale.getValueForPixel(evt.offsetX);
      if (!xValue) return;

      const selectedTime = new Date(xValue);
      const minTime = new Date(selectedTime.getTime() - 30 * 60 * 1000);
      const maxTime = new Date(selectedTime.getTime() + 30 * 60 * 1000);

      const filtered = summaryData.filter(d => {
        const t = new Date(d.uploadTime);
        return t >= minTime && t <= maxTime;
      });

      if (filtered.length > 0) {
        previousScrollY = window.scrollY;
        previousScrollX = window.scrollX;
        renderChart(filtered, "±30min desde " + selectedTime.getHours() + ":00");
        backBtn.style.display = 'block';
      }
    };
  }

  window.resetToDailyView = function resetToDailyView() {
    renderChartForDate(currentDate);
    backBtn.style.display = 'none';
    window.scrollTo(previousScrollX, previousScrollY);
  };

  function renderChartForDate(date) {
    currentDate = date;
    const filtered = summaryData.filter(d =>
      d.uploadTime.startsWith(date)
    ).sort((a, b) => new Date(a.uploadTime) - new Date(b.uploadTime));

    renderChart(filtered, "% Indisponibilidad – " + date);
  }

  datePicker.addEventListener('change', () => {
    renderChartForDate(datePicker.value);
    backBtn.style.display = 'none';
  });

  renderChartForDate(defaultDate);
}