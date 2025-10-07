let exceptionData = [
  {#each exceptions}
    {
      exceptionType: "{it.type}",
      count: {it.count},
      timestamp: "{it.timestamp}"
    }{#if !it_isLast},{/if}
  {/each}
];

let chart;

function renderExceptionChart(data, title) {
  const canvas = document.getElementById("exceptionsChart");
  const ctx = canvas.getContext("2d");
  if (chart) chart.destroy();

  chart = new Chart(ctx, {
    type: "line",
    data: {
      datasets: [
        {
          label: title,
          data: data.map(d => ({
            x: d.timestamp,
            y: d.count,
            label: d.exceptionType
          })),
          borderColor: "#f87171",
          backgroundColor: "rgba(248, 113, 113, 0.3)",
          pointBorderColor: "#fff",
          pointHoverRadius: 7,
          borderWidth: 2,
          tension: 0.4,
          fill: false
        }
      ]
    },
    options: {
      onClick: (evt, elements) => {
        if (elements.length > 0) {
          const idx = elements[0].index;
          const item = chart.data.datasets[0].data[idx];
          window.location.href = "/exceptions-detail?type=" + encodeURIComponent(item.label);
        }
      },
      scales: {
        x: {
          type: "time",
          time: {
            unit: "minute",
            displayFormats: { minute: "HH:mm" }
          },
          title: { display: true, text: "Hora" }
        },
        y: {
          beginAtZero: true,
          title: { display: true, text: "Cantidad de excepciones" }
        }
      }
    }
  });
}

document.addEventListener("DOMContentLoaded", function () {
  const uniqueDates = [...new Set(exceptionData.map(d => d.timestamp.split("T")[0]))];
  const datePicker = document.getElementById("datePicker");
  const today = new Date().toISOString().split("T")[0];

  datePicker.min = uniqueDates[0];
  datePicker.max = uniqueDates[uniqueDates.length - 1];
  datePicker.value = uniqueDates.includes(today) ? today : uniqueDates[0];

  function renderForDate(date) {
    const filtered = exceptionData.filter(e => e.timestamp.startsWith(date));
    renderExceptionChart(filtered, "📉 Excepciones Java – " + date);
  }

  datePicker.addEventListener("change", () => renderForDate(datePicker.value));
  renderForDate(datePicker.value);
});
