#!/usr/bin/env bash
# No 'set -e' here so a failure won't stop the loop
set -u
cd "$(dirname "$0")"

echo "Compiling BudgetTool.java..."
if ! javac BudgetTool.java; then
  echo "Compile failed. Fix errors in BudgetTool.java and re-run."
  exit 1
fi

echo
echo "Discovering tests..."
shopt -s nullglob
tests=(tests/t*.txt)
printf 'Found %d test(s):\n' "${#tests[@]}"
printf ' - %s\n' "${tests[@]}"
echo

echo "Running GradGoals Budget Tool tests..."
echo

passes=0
fails=0
mkdir -p tests/results tests/logs

for file in "${tests[@]}"; do
  name="$(basename "$file")"
  echo "===== Running $name ====="
  log="tests/logs/${name%.txt}.log"

  if java BudgetTool < "$file" > "$log" 2>&1; then
    status=0
  else
    status=$?
  fi

  if grep -q "CSV exported" "$log"; then
    echo "PASS: $name"
    passes=$((passes+1))
    mv -f budget_report_*.csv tests/results/ 2>/dev/null || true
  else
    echo "FAIL: $name (exit $status)"
    echo "Last lines of log:"
    tail -n 15 "$log" || true
    fails=$((fails+1))
  fi
  echo
done

echo "Summary: $passes passed, $fails failed."
echo "Logs:    tests/logs/"
echo "CSVs:    tests/results/"
